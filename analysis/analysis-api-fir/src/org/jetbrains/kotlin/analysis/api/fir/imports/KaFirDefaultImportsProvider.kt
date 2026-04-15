/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir.imports

import org.jetbrains.kotlin.analysis.api.impl.base.imports.KaBaseDefaultImportsProvider
import org.jetbrains.kotlin.analysis.api.platform.projectStructure.KaModulePlatformKind
import org.jetbrains.kotlin.analysis.api.platform.projectStructure.toModulePlatformKind
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.getWasmTarget
import org.jetbrains.kotlin.analyzer.common.CommonDefaultImportsProvider
import org.jetbrains.kotlin.fir.resolve.FirJvmDefaultImportsProvider
import org.jetbrains.kotlin.js.resolve.JsDefaultImportsProvider
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.wasm.WasmTarget
import org.jetbrains.kotlin.resolve.DefaultImportsProvider
import org.jetbrains.kotlin.resolve.konan.platform.NativeDefaultImportsProvider
import org.jetbrains.kotlin.wasm.resolve.WasmJsDefaultImportsProvider
import org.jetbrains.kotlin.wasm.resolve.WasmWasiDefaultImportsProvider

// K1 implementation is in IDE: `org.jetbrains.kotlin.base.fe10.analysis.KaFe10DefaultImportsProvider`
internal class KaFirDefaultImportsProvider : KaBaseDefaultImportsProvider() {
    override fun getCompilerDefaultImportsProvider(targetPlatform: TargetPlatform): DefaultImportsProvider =
        when (targetPlatform.toModulePlatformKind()) {
            KaModulePlatformKind.JVM -> FirJvmDefaultImportsProvider
            KaModulePlatformKind.JS -> JsDefaultImportsProvider
            KaModulePlatformKind.NATIVE -> NativeDefaultImportsProvider
            KaModulePlatformKind.WASM -> when (targetPlatform.getWasmTarget()) {
                WasmTarget.JS -> WasmJsDefaultImportsProvider
                WasmTarget.WASI -> WasmWasiDefaultImportsProvider
            }
            KaModulePlatformKind.METADATA -> CommonDefaultImportsProvider
        }
}
