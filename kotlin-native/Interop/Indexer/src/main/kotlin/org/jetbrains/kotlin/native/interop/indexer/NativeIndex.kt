/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.native.interop.indexer

enum class Language(val sourceFileExtension: String, val clangLanguageName: String) {
    C("c", "c"),
    CPP("cpp", "c++"),
    OBJECTIVE_C("m", "objective-c")
}

interface HeaderInclusionPolicy {
    /**
     * Whether unused declarations from given header should be excluded.
     *
     * @param headerName header path relative to the appropriate include path element (e.g. `time.h` or `curl/curl.h`),
     * or `null` for builtin declarations.
     */
    fun excludeUnused(headerName: String?): Boolean
}

interface HeaderExclusionPolicy {
    /**
     * Whether all declarations from this header should be excluded.
     *
     * Note: the declarations from such headers can be actually present in the internal representation,
     * but not included into the root collections.
     */
    fun excludeAll(headerId: HeaderId): Boolean
}

sealed class NativeLibraryHeaderFilter {
    class NameBased(
            val policy: HeaderInclusionPolicy,
            val excludeDepdendentModules: Boolean
    ) : NativeLibraryHeaderFilter()

    class Predefined(val headers: Set<String>, val modules: List<String>) : NativeLibraryHeaderFilter()
}

interface Compilation {
    val includes: List<IncludeInfo>
    val additionalPreambleLines: List<String>
    val compilerArgs: List<String>
    val language: Language
}

fun defaultCompilerArgs(language: Language): List<String> =
        listOf(
                // We compile with -O2 because Clang may insert inline asm in bitcode at -O0.
                // It is undesirable in case of watchos_arm64 since we target armv7k
                // for this target instead of arm64_32 because it is not supported in LLVM 8.
                //
                // Note that PCH and the *.c file should be compiled with the same optimization level.
                "-O2",
                // Allow throwing exceptions through generated stubs.
                "-fexceptions",
        ) + when (language) {
            Language.C -> emptyList()
            Language.CPP -> emptyList()
            Language.OBJECTIVE_C -> listOf(
                    // "Objective-C" within interop means "Objective-C with ARC":
                    "-fobjc-arc",
                    // Using this flag here has two effects:
                    // 1. The headers are parsed with ARC enabled, thus the API is visible correctly.
                    // 2. The generated Objective-C stubs are compiled with ARC enabled, so reference counting
                    // calls are inserted automatically.

                    // Workaround for https://youtrack.jetbrains.com/issue/KT-48807.
                    // TODO(LLVM): Remove after update to a version with
                    //  https://github.com/apple/llvm-project/commit/2de0a18a8949f0235fb3a08dcc55ff3aa7d969e7.
                    "-DNS_FORMAT_ARGUMENT(A)="
            )
        }

data class CompilationWithPCH(
        override val compilerArgs: List<String>,
        override val language: Language
) : Compilation {
    constructor(compilerArgs: List<String>, precompiledHeader: String, language: Language)
            : this(compilerArgs + listOf("-include-pch", precompiledHeader), language)

    override val includes: List<IncludeInfo>
        get() = emptyList()

    override val additionalPreambleLines: List<String>
        get() = emptyList()
}

/**
 *
 *  @param objCClassesIncludingCategories Objective-C classes that should be merged with categories from the same file.
 *
 * TODO: Compilation hierarchy seems to require some refactoring.
 */
data class NativeLibrary(
        override val includes: List<IncludeInfo>,
        override val additionalPreambleLines: List<String>,
        override val compilerArgs: List<String>,
        val headerToIdMapper: HeaderToIdMapper,
        override val language: Language,
        val excludeSystemLibs: Boolean, // TODO: drop?
        val headerExclusionPolicy: HeaderExclusionPolicy,
        val headerFilter: NativeLibraryHeaderFilter,
        val objCClassesIncludingCategories: Set<String>,
        val allowIncludingObjCCategoriesFromDefFile: Boolean,
) : Compilation

data class IndexerResult(val index: NativeIndex, val compilation: Compilation)

/**
 * Retrieves the definitions from given C header file using given compiler arguments (e.g. defines).
 */
fun buildNativeIndex(library: NativeLibrary, verbose: Boolean, allowPrecompiledHeaders: Boolean = true): IndexerResult = buildNativeIndexImpl(library, verbose, allowPrecompiledHeaders)

/**
 * This class describes the IR of definitions from C header file(s).
 */
abstract class NativeIndex {
    abstract val structs: Collection<StructDecl>
    abstract val enums: Collection<EnumDef>
    abstract val objCClasses: Collection<ObjCClass>
    abstract val objCProtocols: Collection<ObjCProtocol>
    abstract val objCCategories: Collection<ObjCCategory>
    abstract val typedefs: Collection<TypedefDef>
    abstract val functions: Collection<FunctionDecl>
    abstract val macroConstants: Collection<ConstantDef>
    abstract val wrappedMacros: Collection<WrappedMacroDef>
    abstract val globals: Collection<GlobalDecl>
    abstract val includedHeaders: Collection<HeaderId>
}

/**
 * The (contents-based) header id.
 * Its [value] remains valid across different runs of the indexer and the process,
 * and thus can be used to 'serialize' the id.
 */
data class HeaderId(val value: String)

data class Location(val headerId: HeaderId)

interface LocatableDeclaration {
    val location: Location
}

interface TypeDeclaration : LocatableDeclaration

sealed class StructMember(val name: String) {
    abstract val offset: Long?
}

/**
 * C struct field.
 */
class Field(name: String, val type: Type, override val offset: Long, val typeSize: Long, val typeAlign: Long)
    : StructMember(name)

val Field.isAligned: Boolean
    get() = offset % (typeAlign * 8) == 0L

class BitField(name: String, val type: Type, override val offset: Long, val size: Int) : StructMember(name)

class IncompleteField(name: String) : StructMember(name) {
    override val offset: Long? get() = null
}

class AnonymousInnerRecord(val def: StructDef) : StructMember("") {
    override val offset: Long? get() = null
    val typeSize: Long = def.size
}

/**
 * C struct declaration.
 */
abstract class StructDecl(val spelling: String) : TypeDeclaration {

    abstract val def: StructDef?
    abstract val isAnonymous: Boolean
}

/**
 * C struct definition.
 *
 * @param hasNaturalLayout must be `false` if the struct has unnatural layout, e.g. it is `packed`.
 * May be `false` even if the struct has natural layout.
 */
abstract class StructDef(val size: Long, val align: Int) {

    enum class Kind {
        STRUCT, UNION, CLASS
    }

    abstract val kind: Kind
    abstract val members: List<StructMember>
    abstract val methods: List<FunctionDecl>
    abstract val staticFields: List<GlobalDecl>

    val fields: List<Field>
        get() = mutableListOf<Field>().apply {
            members.forEach {
                when (it) {
                    is Field -> add(it)
                    is AnonymousInnerRecord -> addAll(it.def.fields)
                    is BitField,
                    is IncompleteField -> {}
                }
            }
        }

    val bitFields: List<BitField>
        get() = mutableListOf<BitField>().apply {
            members.forEach {
                when (it) {
                    is BitField -> add(it)
                    is AnonymousInnerRecord -> addAll(it.def.bitFields)
                    is Field,
                    is IncompleteField -> {}
                }
            }
        }
}

/**
 * C enum value.
 */
class EnumConstant(val name: String, val value: Long, val isExplicitlyDefined: Boolean)

/**
 * C enum definition.
 */
abstract class EnumDef(val spelling: String, val baseType: Type) : TypeDeclaration {

    abstract val constants: List<EnumConstant>
    abstract val isAnonymous: Boolean
}

sealed class ObjCContainer {
    abstract val protocols: List<ObjCProtocol>
    abstract val methods: List<ObjCMethod>
    abstract val properties: List<ObjCProperty>
}

sealed class ObjCClassOrProtocol(val name: String) : ObjCContainer(), TypeDeclaration {
    abstract val isForwardDeclaration: Boolean
}

data class ObjCMethod(
        val selector: String, val encoding: String, val parameters: List<Parameter>, private val returnType: Type,
        val isVariadic: Boolean, val isClass: Boolean, val nsConsumesSelf: Boolean, val nsReturnsRetained: Boolean,
        val isOptional: Boolean, val isInit: Boolean, val isExplicitlyDesignatedInitializer: Boolean, val isDirect: Boolean
) {

    fun containsInstancetype(): Boolean = returnType.containsInstancetype() // Clang doesn't allow parameter types to use instancetype.

    fun getReturnType(container: ObjCClassOrProtocol): Type = if (returnType.containsInstancetype()) {
        returnType.substituteInstancetype(container)
    } else {
        // Fast path, avoid allocating copies.
        returnType
    }
}

// Clang seems to allow using instancetype only inside certain kinds of types.
// The implementation below therefore covers only particular cases, based on the experiments with Clang and common sense.
private fun Type.containsInstancetype(): Boolean = when (this) {
    is ObjCInstanceType -> true

    is ObjCBlockPointer -> this.returnType.containsInstancetype()
    is FunctionType -> this.returnType.containsInstancetype()
    is PointerType -> this.pointeeType.containsInstancetype()

    else -> false
}

private fun Type.substituteInstancetype(container: ObjCClassOrProtocol): Type = when (this) {
    is ObjCInstanceType -> when (container) {
        is ObjCClass -> ObjCObjectPointer(container, this.nullability, protocols = emptyList())
        is ObjCProtocol -> ObjCIdType(this.nullability, protocols = listOf(container))
    }

    is ObjCBlockPointer -> this.copy(returnType = this.returnType.substituteInstancetype(container))
    is FunctionType -> this.copy(returnType = this.returnType.substituteInstancetype(container))
    is PointerType -> this.copy(pointeeType = this.pointeeType.substituteInstancetype(container))

    else -> this
}

data class ObjCProperty(val name: String, val getter: ObjCMethod, val setter: ObjCMethod?) {
    fun getType(container: ObjCClassOrProtocol): Type = getter.getReturnType(container)
}

abstract class ObjCClass(name: String) : ObjCClassOrProtocol(name) {
    abstract val binaryName: String?
    abstract val baseClass: ObjCClass?
    /**
     * Categories whose methods and properties should be generated as members of Kotlin class.
     */
    abstract val includedCategories: List<ObjCCategory>
}
abstract class ObjCProtocol(name: String) : ObjCClassOrProtocol(name)

abstract class ObjCCategory(val name: String, val clazz: ObjCClass) : ObjCContainer(), LocatableDeclaration

/**
 * C function parameter.
 */
data class Parameter(val name: String?, val type: Type, val nsConsumed: Boolean)

/**
 * C function declaration.
 */
class FunctionDecl(val name: String, val parameters: List<Parameter>, val returnType: Type,
                   val isVararg: Boolean,
                   val parentName: String? = null) {

    val fullName: String = parentName?.let { "$parentName::$name" } ?: name
}

/**
 * C typedef definition.
 *
 * ```
 * typedef $aliased $name;
 * ```
 */
class TypedefDef(val aliased: Type, val name: String, override val location: Location) : TypeDeclaration

abstract class MacroDef(val name: String)

abstract class ConstantDef(name: String, val type: Type): MacroDef(name)
class IntegerConstantDef(name: String, type: Type, val value: Long) : ConstantDef(name, type)
class FloatingConstantDef(name: String, type: Type, val value: Double) : ConstantDef(name, type)
class StringConstantDef(name: String, type: Type, val value: String) : ConstantDef(name, type)

class WrappedMacroDef(name: String, val type: Type) : MacroDef(name)

class GlobalDecl(val name: String, val type: Type, val isConst: Boolean, val parentName: String? = null) {
    val fullName: String get() = parentName?.let { "$it::$name" } ?: name
}

/**
 * C type.
 */
interface Type

interface PrimitiveType : Type

object CharType : PrimitiveType

open class BoolType: PrimitiveType

object CBoolType : BoolType()

object ObjCBoolType : BoolType()
// We omit `const` qualifier for IntegerType and FloatingType to make `CBridgeGen` simpler.
// See KT-28102.
data class IntegerType(val size: Int, val isSigned: Boolean, val spelling: String) : PrimitiveType

// TODO: floating type is not actually defined entirely by its size.
data class FloatingType(val size: Int, val spelling: String) : PrimitiveType

data class VectorType(val elementType: Type, val elementCount: Int, val spelling: String) : PrimitiveType

object VoidType : Type

data class RecordType(val decl: StructDecl) : Type

data class EnumType(val def: EnumDef) : Type

// when pointer type is provided by clang we'll use ots correct spelling
data class PointerType(val pointeeType: Type, val pointeeIsConst: Boolean = false,
                       val isLVReference: Boolean = false, val spelling: String? = null) : Type
// TODO: refactor type representation and support type modifiers more generally.

data class FunctionType(val parameterTypes: List<Type>, val returnType: Type) : Type

interface ArrayType : Type {
    val elemType: Type
}

data class ConstArrayType(override val elemType: Type, val length: Long) : ArrayType
data class IncompleteArrayType(override val elemType: Type) : ArrayType
data class VariableArrayType(override val elemType: Type) : ArrayType

data class Typedef(val def: TypedefDef) : Type

sealed class ObjCPointer : Type {
    enum class Nullability {
        Nullable, NonNull, Unspecified
    }

    abstract val nullability: Nullability
}

sealed class ObjCQualifiedPointer : ObjCPointer() {
    abstract val protocols: List<ObjCProtocol>
}

data class ObjCObjectPointer(
        val def: ObjCClass,
        override val nullability: Nullability,
        override val protocols: List<ObjCProtocol>
) : ObjCQualifiedPointer()

data class ObjCClassPointer(
        override val nullability: Nullability,
        override val protocols: List<ObjCProtocol>
) : ObjCQualifiedPointer()

data class ObjCIdType(
        override val nullability: Nullability,
        override val protocols: List<ObjCProtocol>
) : ObjCQualifiedPointer()

data class ObjCInstanceType(override val nullability: Nullability) : ObjCPointer()
data class ObjCBlockPointer(
        override val nullability: Nullability,
        val parameterTypes: List<Type>, val returnType: Type
) : ObjCPointer()

object UnsupportedType : Type
