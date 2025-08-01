/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.backend

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.BuiltinSymbolsBase
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.backend.utils.unsubstitutedScope
import org.jetbrains.kotlin.fir.declarations.fullyExpandedClass
import org.jetbrains.kotlin.fir.languageVersionSettings
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.scopes.*
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.IrDiagnosticReporter
import org.jetbrains.kotlin.ir.KtDiagnosticReporterWithImplicitIrBasedContext
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.backend.common.linkage.IrDeserializer
import org.jetbrains.kotlin.fir.lookupTracker
import org.jetbrains.kotlin.fir.recordFqNameLookup
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeAlias
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.util.IdSignature
import org.jetbrains.kotlin.ir.util.ReferenceSymbolTable
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.sourceElement
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addToStdlib.shouldNotBeCalled

class Fir2IrPluginContext(
    private val c: Fir2IrComponents,
    override val irBuiltIns: IrBuiltIns,
    @property:ObsoleteDescriptorBasedAPI override val moduleDescriptor: ModuleDescriptor,
    @property:ObsoleteDescriptorBasedAPI override val symbolTable: ReferenceSymbolTable,
    @property:Deprecated(
        "Consider using diagnosticReporter instead. See https://youtrack.jetbrains.com/issue/KT-78277 for more details",
        level = DeprecationLevel.WARNING
    )
    override val messageCollector: MessageCollector,
    diagnosticReporter: DiagnosticReporter,
) : IrPluginContext {
    companion object {
        private const val ERROR_MESSAGE = "This API is not supported for K2"
    }

    @Deprecated("This API is deprecated. It will be removed after the 2.3 release", level = DeprecationLevel.WARNING)
    @ObsoleteDescriptorBasedAPI
    @FirIncompatiblePluginAPI
    override val bindingContext: BindingContext
        get() = error(ERROR_MESSAGE)

    @Deprecated("This API is deprecated. It will be removed after the 2.3 release", level = DeprecationLevel.WARNING)
    @ObsoleteDescriptorBasedAPI
    @FirIncompatiblePluginAPI
    override val typeTranslator: TypeTranslator
        get() = error(ERROR_MESSAGE)

    override val afterK2: Boolean = true

    override val languageVersionSettings: LanguageVersionSettings
        get() = c.session.languageVersionSettings

    override val platform: TargetPlatform
        get() = c.session.moduleData.platform

    override val symbols: BuiltinSymbolsBase = BuiltinSymbolsBase(irBuiltIns)

    private val symbolProvider: FirSymbolProvider
        get() = c.session.symbolProvider

    override val metadataDeclarationRegistrar: Fir2IrIrGeneratedDeclarationsRegistrar
        get() = c.annotationsFromPluginRegistrar

    override fun referenceClass(classId: ClassId): IrClassSymbol? {
        val firSymbol = symbolProvider.getClassLikeSymbolByClassId(classId) as? FirClassSymbol<*> ?: return null
        return c.classifierStorage.getIrClassSymbol(firSymbol)
    }

    override fun referenceTypeAlias(classId: ClassId): IrTypeAliasSymbol? {
        val firSymbol = symbolProvider.getClassLikeSymbolByClassId(classId) as? FirTypeAliasSymbol ?: return null
        return c.classifierStorage.getIrTypeAliasSymbol(firSymbol)
    }

    override fun referenceConstructors(classId: ClassId): Collection<IrConstructorSymbol> {
        return referenceCallableSymbols(
            classId,
            getCallablesFromScope = { getDeclaredConstructors() },
            getCallablesFromProvider = { shouldNotBeCalled() },
            Fir2IrDeclarationStorage::getIrConstructorSymbol
        )
    }

    override fun referenceFunctions(callableId: CallableId): Collection<IrSimpleFunctionSymbol> {
        return referenceCallableSymbols(
            callableId.classId,
            getCallablesFromScope = { getFunctions(callableId.callableName) },
            getCallablesFromProvider = { getTopLevelFunctionSymbols(callableId.packageName, callableId.callableName) },
            Fir2IrDeclarationStorage::getIrFunctionSymbol
        )
    }

    override fun referenceProperties(callableId: CallableId): Collection<IrPropertySymbol> {
        return referenceCallableSymbols(
            callableId.classId,
            getCallablesFromScope = { getProperties(callableId.callableName).filterIsInstance<FirPropertySymbol>() },
            getCallablesFromProvider = { getTopLevelPropertySymbols(callableId.packageName, callableId.callableName) },
            Fir2IrDeclarationStorage::getIrPropertySymbol
        )
    }

    private inline fun <F : FirCallableSymbol<*>, S : IrSymbol, reified R : S> referenceCallableSymbols(
        classId: ClassId?,
        getCallablesFromScope: FirTypeScope.() -> Collection<F>,
        getCallablesFromProvider: FirSymbolProvider.() -> Collection<F>,
        irExtractor: Fir2IrDeclarationStorage.(F) -> S?,
    ): Collection<R> {
        val callables = if (classId != null) {
            val expandedClass = symbolProvider.getClassLikeSymbolByClassId(classId)
                ?.fullyExpandedClass(c.session)
                ?: return emptyList()
            with(c) { expandedClass.unsubstitutedScope().getCallablesFromScope() }
        } else {
            symbolProvider.getCallablesFromProvider()
        }

        return callables.mapNotNull { c.declarationStorage.irExtractor(it) }.filterIsInstance<R>()
    }

    override fun recordLookup(declaration: IrDeclarationWithName, fromFile: IrFile) {
        val lookupTracker = c.session.lookupTracker ?: return
        val fqName = declaration.fqNameWhenAvailable ?: return
        val firFile = (fromFile.metadata as? FirMetadataSource.File)?.fir ?: return
        val fileSource = firFile.source ?: return
        lookupTracker.recordFqNameLookup(fqName, source = null, fileSource = fileSource)
    }

    @Deprecated("Use messageCollector or diagnosticReporter properties instead", level = DeprecationLevel.ERROR)
    override fun createDiagnosticReporter(pluginId: String): MessageCollector {
        error(ERROR_MESSAGE)
    }

    override val diagnosticReporter: IrDiagnosticReporter =
        KtDiagnosticReporterWithImplicitIrBasedContext(diagnosticReporter, languageVersionSettings)

    @Deprecated("This API is deprecated. It will be removed after the 2.3 release", level = DeprecationLevel.WARNING)
    @FirIncompatiblePluginAPI
    override fun referenceClass(fqName: FqName): IrClassSymbol? {
        error(ERROR_MESSAGE)
    }

    @Deprecated("This API is deprecated. It will be removed after the 2.3 release", level = DeprecationLevel.WARNING)
    @FirIncompatiblePluginAPI
    override fun referenceTypeAlias(fqName: FqName): IrTypeAliasSymbol? {
        error(ERROR_MESSAGE)
    }

    @Deprecated("This API is deprecated. It will be removed after the 2.3 release", level = DeprecationLevel.WARNING)
    @FirIncompatiblePluginAPI
    override fun referenceConstructors(classFqn: FqName): Collection<IrConstructorSymbol> {
        error(ERROR_MESSAGE)
    }

    @Deprecated("This API is deprecated. It will be removed after the 2.3 release", level = DeprecationLevel.WARNING)
    @FirIncompatiblePluginAPI
    override fun referenceFunctions(fqName: FqName): Collection<IrSimpleFunctionSymbol> {
        error(ERROR_MESSAGE)
    }

    @Deprecated("This API is deprecated. It will be removed after the 2.3 release", level = DeprecationLevel.WARNING)
    @FirIncompatiblePluginAPI
    override fun referenceProperties(fqName: FqName): Collection<IrPropertySymbol> {
        error(ERROR_MESSAGE)
    }

    @Deprecated("This API is deprecated. It will be removed after the 2.3 release", level = DeprecationLevel.WARNING)
    @FirIncompatiblePluginAPI
    override fun referenceTopLevel(
        signature: IdSignature,
        kind: IrDeserializer.TopLevelSymbolKind,
        moduleDescriptor: ModuleDescriptor
    ): IrSymbol? {
        error(ERROR_MESSAGE)
    }
}
