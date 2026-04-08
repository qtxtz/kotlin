/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.factory.configuration

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.LLFirDanglingFileSession
import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.LLFirSession
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.fir.scopes.FirKotlinScopeProvider
import org.jetbrains.kotlin.platform.JsPlatform
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.WasmPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatform

/**
 * A configuration covering platform-specific options of a *main* platform.
 *
 * Each configuration is specific to a single "main" platform, such as JVM or Wasm. If a session has multiple platforms (e.g., both JS and
 * Wasm), a metadata configuration is used instead of a platform-specific one. Hence, the configuration is *not* composable, unlike
 * [LLPlatformSessionComponentRegistration][org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.factory.components.LLPlatformSessionComponentRegistration].
 *
 * This "main" platform approach is necessary because some aspects of a session are deeply platform-specific and cannot be composed (in
 * contrast to composable session components). For example, we create a
 * [Java symbol provider][org.jetbrains.kotlin.analysis.low.level.api.fir.symbolProviders.LLFirJavaSymbolProvider] for a JVM "main" session,
 * but not for a metadata session which covers the JVM and Native platforms, because we cannot access Java symbols from Native.
 */
internal interface LLPlatformSessionConfiguration {
    /**
     * Creates the [FirKotlinScopeProvider] for source sessions.
     *
     * The function can be overridden to configure platform-specific scope wrapping (e.g., JVM mapped scopes).
     */
    fun createSourceScopeProvider(): FirKotlinScopeProvider = FirKotlinScopeProvider()

    /**
     * Creates additional, platform-specific symbol providers to include in the session's composite [FirSymbolProvider].
     *
     * For dangling file sessions, [createPlatformSpecificSymbolProvidersForDanglingFileSession] is called instead.
     *
     * For binary library sessions, [createBinaryLibrarySymbolProviders] is called instead.
     */
    fun createPlatformSpecificSymbolProviders(
        session: LLFirSession,
        contentScope: GlobalSearchScope,
    ): List<FirSymbolProvider> = emptyList()

    /**
     * Creates additional, platform-specific symbol providers to include in the dangling file session's composite [FirSymbolProvider].
     *
     * In contrast to [createPlatformSpecificSymbolProviders], this function receives the [contextSession] of the dangling file's context
     * module, from which symbol providers might be reused or adapted.
     */
    fun createPlatformSpecificSymbolProvidersForDanglingFileSession(
        session: LLFirDanglingFileSession,
        contextSession: LLFirSession,
    ): List<FirSymbolProvider> = emptyList()

    /**
     * Creates the symbol providers for binary library sessions.
     *
     * In contrast to the platform-specific endpoints above which create additional symbol providers, this function determines the full
     * list of symbol providers for the binary library session's own providers.
     */
    fun createBinaryLibrarySymbolProviders(session: LLFirSession, scope: GlobalSearchScope): List<FirSymbolProvider>

    companion object {
        fun forPlatform(targetPlatform: TargetPlatform, project: Project): LLPlatformSessionConfiguration = when {
            targetPlatform.all { it is JvmPlatform } -> LLJvmSessionConfiguration(project)
            targetPlatform.all { it is JsPlatform } -> LLJsSessionConfiguration(project)
            targetPlatform.all { it is WasmPlatform } -> LLWasmSessionConfiguration(project)
            targetPlatform.all { it is NativePlatform } -> LLNativeSessionConfiguration(project)
            else -> LLCommonSessionConfiguration(project)
        }
    }
}
