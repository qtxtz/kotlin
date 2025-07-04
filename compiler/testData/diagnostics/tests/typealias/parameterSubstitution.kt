// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
class C<T>

typealias CA<T> = C<T>

val ca1: CA<Int> = C<Int>()
val ca2: CA<CA<Int>> = C<C<Int>>()
val ca3: CA<C<Int>> = C<C<Int>>()
val ca4: CA<Int?> = C<Int?>()

/* GENERATED_FIR_TAGS: classDeclaration, nullableType, propertyDeclaration, typeAliasDeclaration,
typeAliasDeclarationWithTypeParameter, typeParameter */
