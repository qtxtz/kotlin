/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.pipeline.web.js

import org.jetbrains.kotlin.backend.common.CompilationException
import org.jetbrains.kotlin.cli.common.reportCompilationException
import org.jetbrains.kotlin.cli.pipeline.PerformanceNotifications
import org.jetbrains.kotlin.cli.pipeline.PipelinePhase
import org.jetbrains.kotlin.cli.pipeline.web.JsBackendPipelineArtifact
import org.jetbrains.kotlin.cli.pipeline.web.JsLoweredIrPipelineArtifact
import org.jetbrains.kotlin.ir.backend.js.transformers.irToJs.IrModuleToJsTransformer
import org.jetbrains.kotlin.ir.backend.js.transformers.irToJs.TranslationMode
import org.jetbrains.kotlin.js.config.artifactConfiguration
import org.jetbrains.kotlin.js.config.dce
import org.jetbrains.kotlin.js.config.minimizedMemberNames

object JsCodegenPipelinePhase : PipelinePhase<JsLoweredIrPipelineArtifact, JsBackendPipelineArtifact>(
    name = "JsCodegenPipelinePhase",
    preActions = setOf(PerformanceNotifications.BackendStarted),
    postActions = setOf(PerformanceNotifications.BackendFinished),
) {
    override fun executePhase(input: JsLoweredIrPipelineArtifact): JsBackendPipelineArtifact? =
        try {
            runCodegen(input)
        } catch (e: CompilationException) {
            input.configuration.reportCompilationException(e)
            null
        }

    private fun runCodegen(input: JsLoweredIrPipelineArtifact): JsBackendPipelineArtifact {
        val configuration = input.configuration
        val artifactConfiguration = configuration.artifactConfiguration ?: error("Missing artifact configuration")
        val transformer = IrModuleToJsTransformer(input.context, input.moduleFragmentToUniqueName)
        val mode = TranslationMode.fromFlags(configuration.dce, artifactConfiguration.granularity, configuration.minimizedMemberNames)
        val codeGenerator = transformer.makeJsCodeGenerator(input.allModules, mode)
        val outputs = codeGenerator.generateJsCode(relativeRequirePath = true, outJsProgram = false)
        return JsBackendPipelineArtifact(outputs, artifactConfiguration, configuration)
    }
}
