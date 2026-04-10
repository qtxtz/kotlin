/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.pipeline.web.js

import org.jetbrains.kotlin.backend.common.CompilationException
import org.jetbrains.kotlin.backend.common.linkage.issues.checkNoUnboundSymbols
import org.jetbrains.kotlin.cli.common.reportCompilationException
import org.jetbrains.kotlin.cli.js.resolve
import org.jetbrains.kotlin.cli.pipeline.PipelinePhase
import org.jetbrains.kotlin.cli.pipeline.web.JsLoweredIrPipelineArtifact
import org.jetbrains.kotlin.cli.pipeline.web.WebLoadedIrPipelineArtifact
import org.jetbrains.kotlin.config.perfManager
import org.jetbrains.kotlin.config.phaser.PhaserState
import org.jetbrains.kotlin.ir.backend.js.*
import org.jetbrains.kotlin.ir.backend.js.lower.collectNativeImplementations
import org.jetbrains.kotlin.ir.backend.js.lower.generateJsTests
import org.jetbrains.kotlin.ir.backend.js.lower.moveBodilessDeclarationsToSeparatePlace
import org.jetbrains.kotlin.ir.backend.js.lower.serialization.ir.JsIrLinker
import org.jetbrains.kotlin.ir.util.ExternalDependenciesGenerator
import org.jetbrains.kotlin.js.config.*
import org.jetbrains.kotlin.util.PhaseType
import org.jetbrains.kotlin.util.PotentiallyIncorrectPhaseTimeMeasurement
import org.jetbrains.kotlin.util.tryMeasurePhaseTime

object JsIrLoweringPipelinePhase : PipelinePhase<WebLoadedIrPipelineArtifact, JsLoweredIrPipelineArtifact>(
    name = "JsIrLoweringPipelinePhase",
) {
    override fun executePhase(input: WebLoadedIrPipelineArtifact): JsLoweredIrPipelineArtifact? =
        try {
            lowerIr(input)
        } catch (e: CompilationException) {
            input.configuration.reportCompilationException(e)
            null
        }

    private fun lowerIr(input: WebLoadedIrPipelineArtifact): JsLoweredIrPipelineArtifact {
        val configuration = input.configuration
        val module = input.moduleStructure
        val (moduleFragment, moduleDependencies, irBuiltIns, symbolTable, deserializer) =
            input.moduleInfo
        require(deserializer is JsIrLinker) {
            "jsCompiler needs JsIrLinker, but got ${deserializer.javaClass.name}"
        }
        val moduleDescriptor = moduleFragment.descriptor
        val irFactory = symbolTable.irFactory
        val shouldGeneratePolyfills = configuration.getBoolean(JSConfigurationKeys.GENERATE_POLYFILLS)
        val performanceManager = configuration.perfManager
        val context = JsIrBackendContext(
            moduleDescriptor,
            irBuiltIns = irBuiltIns,
            symbolTable = symbolTable,
            additionalExportedDeclarationNames = configuration.additionalExportedDeclarationNames,
            keep = configuration.keep.toSet(),
            configuration = configuration,
            dceRuntimeDiagnostic = RuntimeDiagnostic.resolve(configuration.dceRuntimeDiagnostic, configuration),
            safeExternalBoolean = configuration.safeExternalBoolean,
            safeExternalBooleanDiagnostic = RuntimeDiagnostic.resolve(configuration.safeExternalBooleanDiagnostic, configuration),
            incrementalCacheEnabled = false,
        )
        // Load declarations referenced during `context` initialization
        val irProviders = listOf(element = deserializer)
        ExternalDependenciesGenerator(symbolTable = symbolTable, irProviders = irProviders).generateUnboundSymbolsAsDependencies()
        deserializer.postProcess(inOrAfterLinkageStep = true)
        deserializer.checkNoUnboundSymbols(symbolTable = symbolTable, whenDetected = "at the end of IR linkage process")
        deserializer.clear()
        // Sort dependencies after IR linkage.
        val sortedModuleDependencies = deserializer.moduleDependencyTracker.reverseTopoOrder(moduleDependencies = moduleDependencies)
        val allModules = when (module.mainModule) {
            is MainModule.SourceFiles -> error("Main module must be klib")
            is MainModule.Klib -> sortedModuleDependencies.all
        }
        allModules.forEach { module ->
            if (shouldGeneratePolyfills) {
                collectNativeImplementations(context, module)
            }

            moveBodilessDeclarationsToSeparatePlace(context, module)
        }
        // TODO should be done incrementally
        generateJsTests(context, allModules.last())
        @OptIn(PotentiallyIncorrectPhaseTimeMeasurement::class)
        performanceManager?.notifyCurrentPhaseFinishedIfNeeded() // It should be `notifyTranslationToIRFinished`, but this phase not always started or already finished
        performanceManager.tryMeasurePhaseTime(PhaseType.IrLowering) {
            (irFactory.stageController as? WholeWorldStageController)?.let {
                lowerPreservingTags(allModules, context, it)
            } ?: run {
                val phaserState = PhaserState()
                jsLowerings.forEachIndexed { _, lowering ->
                    allModules.forEach { module ->
                        lowering.invoke(context.phaseConfig, phaserState, context, module)
                    }
                }
            }
        }
        return JsLoweredIrPipelineArtifact(
            context,
            mainModule = moduleFragment,
            allModules = allModules,
            moduleFragmentToUniqueName = moduleDependencies.fragmentNames,
            configuration,
        )
    }
}
