/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.wasm.test.converters

import org.jetbrains.kotlin.backend.common.CommonKLibResolver
import org.jetbrains.kotlin.cli.common.messages.getLogger
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.config.messageCollector
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.diagnostics.DiagnosticReporterFactory
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.ir.backend.js.JsFactories
import org.jetbrains.kotlin.ir.backend.js.serializeModuleIntoKlib
import org.jetbrains.kotlin.library.impl.BuiltInsPlatform
import org.jetbrains.kotlin.platform.wasm.WasmTarget
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.test.backend.ir.IrBackendFacade
import org.jetbrains.kotlin.test.backend.ir.IrBackendInput
import org.jetbrains.kotlin.test.directives.KlibBasedCompilerTestDirectives.SKIP_GENERATING_KLIB
import org.jetbrains.kotlin.test.frontend.classic.ModuleDescriptorProvider
import org.jetbrains.kotlin.test.frontend.classic.moduleDescriptorProvider
import org.jetbrains.kotlin.test.frontend.fir.getAllWasmDependenciesPaths
import org.jetbrains.kotlin.test.frontend.fir.resolveLibraries
import org.jetbrains.kotlin.test.model.ArtifactKinds
import org.jetbrains.kotlin.test.model.BinaryArtifacts
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.*
import org.jetbrains.kotlin.test.services.configuration.WasmEnvironmentConfigurator
import org.jetbrains.kotlin.test.services.configuration.getFriendDependencies
import org.jetbrains.kotlin.wasm.config.WasmConfigurationKeys

class FirWasmKlibSerializerFacade(
    testServices: TestServices,
    private val firstTimeCompilation: Boolean
) : IrBackendFacade<BinaryArtifacts.KLib>(testServices, ArtifactKinds.KLib) {

    override val additionalServices: List<ServiceRegistrationData>
        get() = listOf(service(::ModuleDescriptorProvider))

    constructor(testServices: TestServices) : this(testServices, firstTimeCompilation = true)

    override fun shouldTransform(module: TestModule): Boolean {
        return testServices.defaultsProvider.backendKind == inputKind && SKIP_GENERATING_KLIB !in module.directives
    }

    override fun transform(module: TestModule, inputArtifact: IrBackendInput): BinaryArtifacts.KLib {
        require(inputArtifact is IrBackendInput.WasmAfterFrontendBackendInput) {
            "FirWasmKlibSerializerFacade expects IrBackendInput.WasmAfterFrontendBackendInput as input"
        }

        val configuration = testServices.compilerConfigurationProvider.getCompilerConfiguration(module)
        val diagnosticReporter = DiagnosticReporterFactory.createReporter(configuration.messageCollector)
        val outputFile = WasmEnvironmentConfigurator.getKlibArtifactFile(testServices, module.name)

        // TODO: consider avoiding repeated libraries resolution
        val target = configuration.get(WasmConfigurationKeys.WASM_TARGET, WasmTarget.JS)
        val libraries = resolveLibraries(configuration, getAllWasmDependenciesPaths(module, testServices, target))

        if (firstTimeCompilation) {
            serializeModuleIntoKlib(
                moduleName = configuration[CommonConfigurationKeys.MODULE_NAME]!!,
                configuration = configuration,
                diagnosticReporter = diagnosticReporter,
                metadataSerializer = inputArtifact.metadataSerializer,
                klibPath = outputFile.path,
                dependencies = libraries.map { it.library },
                moduleFragment = inputArtifact.irModuleFragment,
                irBuiltIns = inputArtifact.irPluginContext.irBuiltIns,
                cleanFiles = inputArtifact.icData,
                nopack = true,
                containsErrorCode = inputArtifact.hasErrors,
                jsOutputName = null,
                builtInsPlatform = BuiltInsPlatform.WASM,
                wasmTarget = target,
            )
        }

        // TODO: consider avoiding repeated libraries resolution
        val lib = CommonKLibResolver.resolve(
            getAllWasmDependenciesPaths(module, testServices, target) + listOf(outputFile.path),
            configuration.getLogger(treatWarningsAsErrors = true)
        ).getFullResolvedList().last().library

        val moduleDescriptor = JsFactories.DefaultDeserializedDescriptorFactory.createDescriptorOptionalBuiltIns(
            lib,
            configuration.languageVersionSettings,
            LockBasedStorageManager("ModulesStructure"),
            inputArtifact.irModuleFragment.descriptor.builtIns,
            packageAccessHandler = null,
            lookupTracker = LookupTracker.DO_NOTHING
        )

        moduleDescriptor.setDependencies(
            inputArtifact.irModuleFragment.descriptor.allDependencyModules.filterIsInstance<ModuleDescriptorImpl>() + moduleDescriptor,
            getFriendDependencies(module, testServices),
        )

        testServices.moduleDescriptorProvider.replaceModuleDescriptorForModule(module, moduleDescriptor)
        testServices.libraryProvider.setDescriptorAndLibraryByName(outputFile.path, moduleDescriptor, lib)

        return BinaryArtifacts.KLib(outputFile, diagnosticReporter)
    }
}
