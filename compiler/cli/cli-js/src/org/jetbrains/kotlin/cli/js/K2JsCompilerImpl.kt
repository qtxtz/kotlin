/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.js

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.pipeline.web.JsLoweredIrPipelineArtifact
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.perfManager
import org.jetbrains.kotlin.ir.backend.js.ModulesStructure
import org.jetbrains.kotlin.ir.backend.js.transformers.irToJs.CompilationOutputsBuilt
import org.jetbrains.kotlin.ir.backend.js.transformers.irToJs.IrModuleToJsTransformer
import org.jetbrains.kotlin.ir.backend.js.transformers.irToJs.JsCodeGenerator
import org.jetbrains.kotlin.ir.backend.js.transformers.irToJs.TranslationMode
import org.jetbrains.kotlin.js.config.JsGenerationGranularity
import org.jetbrains.kotlin.js.config.artifactConfiguration
import org.jetbrains.kotlin.js.config.dce
import org.jetbrains.kotlin.js.config.minimizedMemberNames
import org.jetbrains.kotlin.util.PhaseType

class Ir2JsTransformer private constructor(
    val module: ModulesStructure,
    val messageCollector: MessageCollector,
    val configuration: CompilerConfiguration,
    val mainCallArguments: List<String>?,
    val granularity: JsGenerationGranularity,
    val dce: Boolean,
    val minimizedMemberNames: Boolean,
) {
    constructor(
        configuration: CompilerConfiguration,
        module: ModulesStructure,
        messageCollector: MessageCollector,
        mainCallArguments: List<String>?,
    ) : this(
        module,
        messageCollector,
        configuration,
        mainCallArguments,
        granularity = configuration.artifactConfiguration!!.granularity,
        dce = configuration.dce,
        minimizedMemberNames = configuration.minimizedMemberNames,
    )

    private val performanceManager = module.compilerConfiguration.perfManager

    private fun makeJsCodeGenerator(ir: JsLoweredIrPipelineArtifact): JsCodeGenerator {
        val transformer = IrModuleToJsTransformer(ir.context, ir.moduleFragmentToUniqueName, mainCallArguments != null)

        val mode = TranslationMode.fromFlags(dce, granularity, minimizedMemberNames)
        return transformer
            .also { performanceManager?.notifyPhaseStarted(PhaseType.Backend) }
            .makeJsCodeGenerator(ir.allModules, mode)
    }

    fun compileAndTransformIrNew(loweredIr: JsLoweredIrPipelineArtifact): CompilationOutputsBuilt {
        return makeJsCodeGenerator(loweredIr)
            .generateJsCode(relativeRequirePath = true, outJsProgram = false)
            .also {
                performanceManager?.notifyPhaseFinished(PhaseType.Backend)
            }
    }
}
