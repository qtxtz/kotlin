/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.pipeline.web

import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.backend.common.linkage.partial.setupPartialLinkageConfig
import org.jetbrains.kotlin.cli.common.allowKotlinPackage
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.cliArgument
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.common.createPhaseConfig
import org.jetbrains.kotlin.cli.common.incrementalCompilationIsEnabledForJs
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.WARNING
import org.jetbrains.kotlin.cli.common.setupCommonKlibArguments
import org.jetbrains.kotlin.cli.js.*
import org.jetbrains.kotlin.cli.pipeline.AbstractConfigurationPhase
import org.jetbrains.kotlin.cli.pipeline.ArgumentsPipelineArtifact
import org.jetbrains.kotlin.cli.pipeline.CheckCompilationErrors
import org.jetbrains.kotlin.cli.pipeline.ConfigurationUpdater
import org.jetbrains.kotlin.cli.pipeline.web.js.JsConfigurationUpdater
import org.jetbrains.kotlin.cli.pipeline.web.wasm.WasmConfigurationUpdater
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.incremental.components.ExpectActualTracker
import org.jetbrains.kotlin.incremental.components.ICFileMappingTracker
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.incremental.js.IncrementalDataProvider
import org.jetbrains.kotlin.incremental.js.IncrementalNextRoundChecker
import org.jetbrains.kotlin.incremental.js.IncrementalResultsConsumer
import org.jetbrains.kotlin.js.config.*
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.metadata.deserialization.MetadataVersion
import org.jetbrains.kotlin.platform.wasm.WasmTarget
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.jetbrains.kotlin.wasm.config.WasmConfigurationKeys
import java.io.File
import java.io.IOException

object WebConfigurationPhase : AbstractConfigurationPhase<K2JSCompilerArguments>(
    name = "JsConfigurationPhase",
    postActions = setOf(CheckCompilationErrors.CheckMessageCollector),
    configurationUpdaters = listOf(CommonWebConfigurationUpdater, JsConfigurationUpdater, WasmConfigurationUpdater)
) {
    override fun createMetadataVersion(versionArray: IntArray): BinaryVersion {
        return MetadataVersion(*versionArray)
    }
}

/**
 * Contains configuration updating logic shared between JS and WASM CLIs
 */
object CommonWebConfigurationUpdater : ConfigurationUpdater<K2JSCompilerArguments>() {
    override fun fillConfiguration(
        input: ArgumentsPipelineArtifact<K2JSCompilerArguments>,
        configuration: CompilerConfiguration,
    ) {
        val (arguments, services, rootDisposable, _, _) = input
        setupPlatformSpecificArgumentsAndServices(configuration, arguments, services)
        initializeCommonConfiguration(configuration, arguments)
        configuration.jsIncrementalCompilationEnabled = incrementalCompilationIsEnabledForJs(arguments)

        val messageCollector = configuration.messageCollector
        when (val outputName = arguments.moduleName) {
            null -> messageCollector.report(ERROR, "IR: Specify output name via ${K2JSCompilerArguments::moduleName.cliArgument}", null)
            else -> configuration.outputName = outputName
        }
        when (val outputDir = arguments.outputDir) {
            null -> messageCollector.report(ERROR, "IR: Specify output dir via ${K2JSCompilerArguments::outputDir.cliArgument}", null)
            else -> try {
                configuration.outputDir = File(outputDir).canonicalFile
            } catch (_: IOException) {
                messageCollector.report(ERROR, "Could not resolve output directory", location = null)
            }
        }

        configuration.wasmCompilation = arguments.wasm
        configuration.produceKlibFile = arguments.irProduceKlibFile
        configuration.produceKlibDir = arguments.irProduceKlibDir
        configuration.granularity = arguments.granularity
        configuration.tsCompilationStrategy = arguments.dtsStrategy
        arguments.main?.let { configuration.callMainMode = it }
        configuration.dce = arguments.irDce

        val zipAccessor = DisposableZipFileSystemAccessor(64)
        Disposer.register(rootDisposable, zipAccessor)
        configuration.zipFileSystemAccessor = zipAccessor
        configuration.perModuleOutputName = arguments.irPerModuleOutputName
        configuration.icCacheDirectory = arguments.cacheDirectory
        configuration.icCacheReadOnly = arguments.icCacheReadonly
        configuration.preserveIcOrder = arguments.preserveIcOrder

        // setup phase config for the first compilation stage (KLIB compilation)
        if (arguments.includes == null) {
            configuration.phaseConfig = createPhaseConfig(arguments)
        }

        if (arguments.includes == null && arguments.irProduceJs) {
            configuration.messageCollector.report(
                ERROR,
                "It is not possible to produce a KLIB ('${K2JSCompilerArguments::includes.cliArgument}' is not passed) "
                        + "and compile the resulting JavaScript artifact ('${K2JSCompilerArguments::irProduceJs.cliArgument}' is passed) at the same time "
                        + "with the K2 compiler"
            )
        }
    }

    /**
     * This part of the configuration update is shared between phased K2 CLI and
     * K1 implementation of [K2JSCompiler.setupPlatformSpecificArgumentsAndServices].
     */
    internal fun setupPlatformSpecificArgumentsAndServices(
        configuration: CompilerConfiguration,
        arguments: K2JSCompilerArguments,
        services: Services,
    ) {
        val messageCollector = configuration.messageCollector

        if (arguments.generateDwarf) {
            configuration.put(WasmConfigurationKeys.WASM_GENERATE_DWARF, true)
        }

        if (arguments.debuggerCustomFormatters) {
            configuration.useDebuggerCustomFormatters = true
        }

        if (arguments.sourceMap) {
            configuration.sourceMap = true
            if (arguments.sourceMapPrefix != null) {
                configuration.sourceMapPrefix = arguments.sourceMapPrefix!!
            }

            var sourceMapSourceRoots = arguments.sourceMapBaseDirs
            if (sourceMapSourceRoots == null && StringUtil.isNotEmpty(arguments.sourceMapPrefix)) {
                sourceMapSourceRoots = calculateSourceMapSourceRoot(messageCollector, arguments)
            }

            if (sourceMapSourceRoots != null) {
                val sourceMapSourceRootList = StringUtil.split(sourceMapSourceRoots, File.pathSeparator)
                configuration.put(JSConfigurationKeys.SOURCE_MAP_SOURCE_ROOTS, sourceMapSourceRootList)
            }

        } else {
            if (arguments.sourceMapPrefix != null) {
                messageCollector.report(WARNING, "source-map-prefix argument has no effect without source map", null)
            }
            if (arguments.sourceMapBaseDirs != null) {
                messageCollector.report(WARNING, "source-map-source-root argument has no effect without source map", null)
            }
        }

        configuration.friendPathsDisabled = arguments.friendModulesDisabled
        configuration.generateDts = arguments.generateDts
        configuration.generateStrictImplicitExport = arguments.strictImplicitExportType

        if (arguments.wasm) {
            // These parameters are not configured during K1 compilation. So, it's necessary to set them up here manually.
            configuration.wasmCompilation = true
            configuration.putIfNotNull(WasmConfigurationKeys.WASM_TARGET, arguments.wasmTarget?.let(WasmTarget::fromName))

            // K/Wasm support ES modules only.
            configuration.moduleKind = ModuleKind.ES
        }

        configuration.incrementalDataProvider = services[IncrementalDataProvider::class.java]
        configuration.incrementalResultsConsumer = services[IncrementalResultsConsumer::class.java]
        configuration.incrementalNextRoundChecker = services[IncrementalNextRoundChecker::class.java]
        configuration.lookupTracker = services[LookupTracker::class.java]
        configuration.expectActualTracker = services[ExpectActualTracker::class.java]
        configuration.fileMappingTracker = services[ICFileMappingTracker::class.java]

        val sourceMapEmbedContentString = arguments.sourceMapEmbedSources
        var sourceMapContentEmbedding: SourceMapSourceEmbedding? = if (sourceMapEmbedContentString != null) {
            sourceMapContentEmbeddingMap[sourceMapEmbedContentString]
        } else {
            SourceMapSourceEmbedding.INLINING
        }
        if (sourceMapContentEmbedding == null) {
            messageCollector.report(
                ERROR,
                "Unknown source map source embedding mode: $sourceMapEmbedContentString. Valid values are: ${sourceMapContentEmbeddingMap.keys.joinToString()}"
            )
            sourceMapContentEmbedding = SourceMapSourceEmbedding.INLINING
        }
        configuration.sourceMapEmbedSources = sourceMapContentEmbedding
        configuration.sourceMapIncludeMappingsFromUnavailableFiles = arguments.includeUnavailableSourcesIntoSourceMap

        if (!arguments.sourceMap && sourceMapEmbedContentString != null) {
            messageCollector.report(WARNING, "source-map-embed-sources argument has no effect without source map", null)
        }

        val sourceMapNamesPolicyString = arguments.sourceMapNamesPolicy
        var sourceMapNamesPolicy: SourceMapNamesPolicy? = if (sourceMapNamesPolicyString != null) {
            sourceMapNamesPolicyMap[sourceMapNamesPolicyString]
        } else {
            SourceMapNamesPolicy.SIMPLE_NAMES
        }
        if (sourceMapNamesPolicy == null) {
            messageCollector.report(
                ERROR,
                "Unknown source map names policy: $sourceMapNamesPolicyString. Valid values are: ${sourceMapNamesPolicyMap.keys.joinToString()}"
            )
            sourceMapNamesPolicy = SourceMapNamesPolicy.SIMPLE_NAMES
        }
        configuration.sourcemapNamesPolicy = sourceMapNamesPolicy

        configuration.printReachabilityInfo = arguments.irDcePrintReachabilityInfo
        configuration.fakeOverrideValidator = arguments.fakeOverrideValidator
        configuration.dumpReachabilityInfoToFile = arguments.irDceDumpReachabilityInfoToFile

        arguments.irDceRuntimeDiagnostic?.let { configuration.dceRuntimeDiagnostic = it }

        configuration.setupPartialLinkageConfig(
            mode = arguments.partialLinkageMode,
            logLevel = arguments.partialLinkageLogLevel,
            compilerModeAllowsUsingPartialLinkage = arguments.includes != null, // no PL when producing KLIB
            onWarning = { messageCollector.report(WARNING, it) },
            onError = { messageCollector.report(ERROR, it) }
        )
    }

    internal fun initializeCommonConfiguration(configuration: CompilerConfiguration, arguments: K2JSCompilerArguments) {
        configuration.setupCommonKlibArguments(arguments, canBeMetadataKlibCompilation = false)

        val libraries: List<String> = configureLibraries(arguments.libraries) + listOfNotNull(arguments.includes)
        val friendLibraries: List<String> = configureLibraries(arguments.friendModules)
        configuration.libraries += libraries
        configuration.friendLibraries += friendLibraries
        arguments.includes?.let { configuration.includes = it }
        val commonSourcesArray = arguments.commonSources
        val commonSources = commonSourcesArray?.toSet() ?: emptySet()
        val hmppCliModuleStructure = configuration.hmppModuleStructure
        for (arg in arguments.freeArgs) {
            configuration.addKotlinSourceRoot(arg, commonSources.contains(arg), hmppCliModuleStructure?.getModuleNameForSource(arg))
        }
        val moduleName = arguments.irModuleName ?: arguments.moduleName ?: run {
            val message = "Specify the module name via ${K2JSCompilerArguments::irModuleName.cliArgument} or ${K2JSCompilerArguments::moduleName.cliArgument}"
            configuration.messageCollector.report(ERROR, message, location = null)
            return
        }
        configuration.moduleName = moduleName
        configuration.allowKotlinPackage = arguments.allowKotlinPackage
    }
}
