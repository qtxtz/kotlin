/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.factory.components

import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.LLFirSession
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.platform.*
import org.jetbrains.kotlin.platform.jvm.JvmPlatform
import org.jetbrains.kotlin.platform.wasm.WasmPlatformWithTarget
import org.jetbrains.kotlin.platform.wasm.WasmTarget

/**
 * Handles the registration of platform-specific session components.
 *
 * The interface should be implemented once per target platform (JVM, JS, Wasm, Native). Registrations are composable, so multiple component
 * registrations can be applied to the same session (for metadata sessions covering multiple platforms).
 */
internal interface LLPlatformSessionComponentRegistration {
    /**
     * Registers components that apply to all platform-aware session kinds (sources, resolvable libraries, binary libraries, dangling
     * files).
     *
     * [platformSpecificSymbolProviders] contains platform-specific symbol providers that are already part of the session's symbol provider,
     * but might have to be registered as platform-specific components, too. For binary library sessions, this list is empty, as their
     * symbol providers are created in a non-standard way.
     */
    fun registerComponents(session: LLFirSession, platformSpecificSymbolProviders: List<FirSymbolProvider>)

    /**
     * Registers components specific to source sessions.
     */
    fun registerSourceComponents(session: LLFirSession) {}

    /**
     * Registers components specific to resolvable library sessions.
     */
    fun registerResolvableLibraryComponents(session: LLFirSession) {}

    /**
     * Registers components specific to binary library sessions.
     */
    fun registerBinaryLibraryComponents(session: LLFirSession) {}

    /**
     * Registers components specific to dangling file sessions.
     */
    fun registerDanglingFileComponents(session: LLFirSession) {}

    companion object {
        fun forPlatform(targetPlatform: TargetPlatform): List<LLPlatformSessionComponentRegistration> = buildList {
            if (targetPlatform.has<JvmPlatform>()) {
                add(LLJvmSessionComponentRegistration)
            }

            if (targetPlatform.has<JsPlatform>()) {
                add(LLJsSessionComponentRegistration)
            }

            if (targetPlatform.has<WasmPlatform>()) {
                val wasmTargets = targetPlatform.subplatformsOfType<WasmPlatform>().mapTo(mutableSetOf()) { platform ->
                    (platform as? WasmPlatformWithTarget)?.target ?: WasmTarget.JS
                }

                wasmTargets.forEach { add(LLWasmSessionComponentRegistration(it)) }
            }

            if (targetPlatform.has<NativePlatform>()) {
                add(LLNativeSessionComponentRegistration)
            }
        }
    }
}
