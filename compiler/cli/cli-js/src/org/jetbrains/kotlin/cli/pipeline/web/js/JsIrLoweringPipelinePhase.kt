/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.pipeline.web.js

import org.jetbrains.kotlin.backend.common.CompilationException
import org.jetbrains.kotlin.cli.common.arguments.K2JsArgumentConstants
import org.jetbrains.kotlin.cli.common.reportCompilationException
import org.jetbrains.kotlin.cli.js.resolve
import org.jetbrains.kotlin.cli.pipeline.PipelinePhase
import org.jetbrains.kotlin.cli.pipeline.web.JsLoweredIrPipelineArtifact
import org.jetbrains.kotlin.cli.pipeline.web.WebLoadedIrPipelineArtifact
import org.jetbrains.kotlin.ir.backend.js.compileIr
import org.jetbrains.kotlin.js.config.*

object JsIrLoweringPipelinePhase : PipelinePhase<WebLoadedIrPipelineArtifact, JsLoweredIrPipelineArtifact>(
    name = "JsIrLoweringPipelinePhase",
) {
    override fun executePhase(input: WebLoadedIrPipelineArtifact): JsLoweredIrPipelineArtifact? {
        val configuration = input.configuration
        val module = input.moduleStructure
        val (moduleFragment, moduleDependencies, irBuiltIns, symbolTable, deserializer) =
            input.moduleInfo
        try {
            val loweredIr = compileIr(
                moduleFragment = moduleFragment,
                mainModule = module.mainModule,
                mainCallArguments = emptyList<String>().takeUnless { configuration.callMainMode == K2JsArgumentConstants.NO_CALL },
                configuration = configuration,
                moduleDependencies = moduleDependencies,
                irBuiltIns = irBuiltIns,
                symbolTable = symbolTable,
                irLinker = deserializer,
                exportedDeclarations = configuration.additionalExportedDeclarationNames,
                keep = configuration.keep.toSet(),
                dceRuntimeDiagnostic = RuntimeDiagnostic.resolve(configuration.dceRuntimeDiagnostic, configuration),
                safeExternalBoolean = configuration.safeExternalBoolean,
                safeExternalBooleanDiagnostic = RuntimeDiagnostic.resolve(configuration.safeExternalBooleanDiagnostic, configuration),
            )
            return JsLoweredIrPipelineArtifact(loweredIr, configuration)
        } catch (e: CompilationException) {
            configuration.reportCompilationException(e)
            return null
        }
    }
}
