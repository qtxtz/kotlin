/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import java.io.File
import kotlin.io.extension
import kotlin.test.assertTrue
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.extensionsStorage
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.compiler.plugin.registerExtension
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.incremental.js.IncrementalResultsConsumerImpl
import org.jetbrains.kotlin.js.config.JsGenerationGranularity
import org.jetbrains.kotlin.js.config.ModuleKind
import org.jetbrains.kotlin.js.config.incrementalResultsConsumer
import org.jetbrains.kotlin.plugin.sandbox.fir.FirPluginPrototypeExtensionRegistrar
import org.jetbrains.kotlin.plugin.sandbox.ir.GeneratedDeclarationsIrBodyFiller
import org.jetbrains.kotlin.test.TargetBackend
import org.junit.jupiter.api.fail

abstract class AbstractIncrementalK2JsWithPluginCompilerRunnerTest(
    targetBackend: TargetBackend,
    granularity: JsGenerationGranularity,
    workingDirPath: String
) : JsAbstractInvalidationTest(targetBackend, granularity, workingDirPath) {
    companion object {
        private const val ANNOTATIONS_KLIB_DIR = "plugins/plugin-sandbox/plugin-annotations/build/libs/"
        private const val ANNOTATIONS_KLIB_NAME = "plugin-annotations"

        private fun findKlib(dir: String, name: String, taskName: String): String {
            val failMessage = { "Jar $name does not exist. Please run $taskName" }
            val libDir = File(dir)
            assertTrue(libDir.exists() && libDir.isDirectory)
            val jar = libDir.listFiles()?.firstOrNull { it.name.startsWith(name) && it.extension == "klib" } ?: fail(failMessage)
            return jar.canonicalPath
        }
    }

    private val annotationsKlib =
        findKlib(ANNOTATIONS_KLIB_DIR, ANNOTATIONS_KLIB_NAME, ":plugins:plugin-sandbox:plugin-annotations:jsJar")

    override val librariesToExcludeFromStats
        get() = super.librariesToExcludeFromStats + annotationsKlib

    override val libraryNamesToExcludeFromStats: Set<String>
        get() = super.libraryNamesToExcludeFromStats + "kotlin_org_jetbrains_kotlin_plugin_annotations"

    @OptIn(ExperimentalCompilerApi::class)
    override fun createConfiguration(
        moduleName: String,
        moduleKind: ModuleKind,
        languageFeatures: List<String>,
        allLibraries: List<String>,
        friendLibraries: List<String>,
        includedLibrary: String?
    ): CompilerConfiguration {
        val copy = super.createConfiguration(
            moduleName,
            moduleKind,
            languageFeatures,
            allLibraries + annotationsKlib,
            friendLibraries,
            includedLibrary
        )
        copy.incrementalResultsConsumer = IncrementalResultsConsumerImpl()
        with(copy.extensionsStorage!!) {
            // Since the IC infrastructure is weird and duplicate emitting of extensions
            if (registeredExtensions.isEmpty()) {
                FirExtensionRegistrar.registerExtension(FirPluginPrototypeExtensionRegistrar())
                IrGenerationExtension.registerExtension(GeneratedDeclarationsIrBodyFiller())
            }
        }
        return copy
    }
}

abstract class AbstractIncrementalK2JsWithPluginSandboxPerModuleTest :
    AbstractIncrementalK2JsWithPluginCompilerRunnerTest(
        TargetBackend.JS_IR,
        JsGenerationGranularity.PER_MODULE,
        "plugin-sandbox/incremental/perModule"
    )

abstract class AbstractIncrementalK2JsEs6WithPluginSandboxPerModuleTest :
    AbstractIncrementalK2JsWithPluginCompilerRunnerTest(
        TargetBackend.JS_IR_ES6,
        JsGenerationGranularity.PER_MODULE,
        "plugin-sandbox/incremental/perModuleEs6"
    )

abstract class AbstractIncrementalK2JsWithPluginSandboxPerFileTest :
    AbstractIncrementalK2JsWithPluginCompilerRunnerTest(
        TargetBackend.JS_IR,
        JsGenerationGranularity.PER_FILE,
        "plugin-sandbox/incremental/perFile"
    )

abstract class AbstractIncrementalK2JsEs6WithPluginSandboxPerFileTest :
    AbstractIncrementalK2JsWithPluginCompilerRunnerTest(
        TargetBackend.JS_IR_ES6,
        JsGenerationGranularity.PER_FILE,
        "plugin-sandbox/incremental/perFileEs6"
    )
