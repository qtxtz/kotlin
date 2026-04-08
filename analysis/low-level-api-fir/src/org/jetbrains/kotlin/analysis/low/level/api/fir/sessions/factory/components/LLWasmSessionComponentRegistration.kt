/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.factory.components

import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.LLFirSession
import org.jetbrains.kotlin.fir.SessionConfiguration
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.fir.session.FirWasmSessionFactory
import org.jetbrains.kotlin.platform.wasm.WasmTarget

@OptIn(SessionConfiguration::class)
internal class LLWasmSessionComponentRegistration(private val wasmTarget: WasmTarget) : LLPlatformSessionComponentRegistration {
    override fun registerComponents(session: LLFirSession, platformSpecificSymbolProviders: List<FirSymbolProvider>) = with(session) {
        // We have to distinguish between WasmJS and WasmWasi and pick the correct session factory.
        with(FirWasmSessionFactory.of(wasmTarget)) {
            registerWasmComponents()
        }
    }
}
