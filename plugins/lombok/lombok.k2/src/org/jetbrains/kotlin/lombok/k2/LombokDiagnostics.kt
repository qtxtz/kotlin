/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.lombok.k2

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.errorWithoutSource
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.BaseSourcelessDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.diagnostics.warningWithoutSource
import org.jetbrains.kotlin.lombok.k2.LombokCliDiagnostics.LOMBOK_CONFIG_IS_MISSING
import org.jetbrains.kotlin.lombok.k2.LombokCliDiagnostics.LOMBOK_PLUGIN_IS_EXPERIMENTAL
import org.jetbrains.kotlin.lombok.k2.LombokCliDiagnostics.UNKNOWN_PLUGIN_OPTION
import org.jetbrains.kotlin.lombok.k2.LombokFirDiagnostics.LOG_FLAG_USAGE_ERROR
import org.jetbrains.kotlin.lombok.k2.LombokFirDiagnostics.LOG_FLAG_USAGE_WARNING
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import kotlin.getValue

object LombokCliDiagnostics : KtDiagnosticsContainer() {
    val LOMBOK_PLUGIN_IS_EXPERIMENTAL by warningWithoutSource()
    val LOMBOK_CONFIG_IS_MISSING by warningWithoutSource()
    val UNKNOWN_PLUGIN_OPTION by errorWithoutSource()

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = LombokCliDiagnosticsMessages
}

object LombokFirDiagnostics : KtDiagnosticsContainer() {
    val LOG_FLAG_USAGE_WARNING by warning0<KtAnnotationEntry>()
    val LOG_FLAG_USAGE_ERROR by error0<KtAnnotationEntry>()

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = LombokFirDiagnosticsMessages
}

object LombokCliDiagnosticsMessages : BaseSourcelessDiagnosticRendererFactory() {
    override val MAP by KtDiagnosticFactoryToRendererMap("CLI") { map ->
        map.put(LOMBOK_PLUGIN_IS_EXPERIMENTAL, MESSAGE_PLACEHOLDER)
        map.put(LOMBOK_CONFIG_IS_MISSING, MESSAGE_PLACEHOLDER)
        map.put(UNKNOWN_PLUGIN_OPTION, MESSAGE_PLACEHOLDER)
    }
}

object LombokFirDiagnosticsMessages : BaseDiagnosticRendererFactory() {
    const val LOG_FLAG_USAGE_MESSAGE = "Use of any @Log is flagged according to lombok configuration."
    override val MAP by KtDiagnosticFactoryToRendererMap("FIR") { map ->
        map.put(LOG_FLAG_USAGE_WARNING, LOG_FLAG_USAGE_MESSAGE)
        map.put(LOG_FLAG_USAGE_ERROR, LOG_FLAG_USAGE_MESSAGE)
    }
}
