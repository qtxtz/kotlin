/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.factory.components

import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.LLFirSession
import org.jetbrains.kotlin.analyzer.common.CommonDefaultImportsProvider
import org.jetbrains.kotlin.fir.SessionConfiguration
import org.jetbrains.kotlin.fir.backend.jvm.FirJvmTypeMapper
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.fir.scopes.FirDefaultImportsProviderHolder
import org.jetbrains.kotlin.fir.scopes.impl.FirEnumEntriesSupport
import org.jetbrains.kotlin.platform.has
import org.jetbrains.kotlin.platform.jvm.JvmPlatform

@OptIn(SessionConfiguration::class)
internal object LLCommonSessionComponentRegistration : LLPlatformSessionComponentRegistration {
    override fun registerComponents(session: LLFirSession, platformSpecificSymbolProviders: List<FirSymbolProvider>) = with(session) {
        register(FirDefaultImportsProviderHolder.of(CommonDefaultImportsProvider))
        register(FirEnumEntriesSupport(this))
        if (ktModule.targetPlatform.has<JvmPlatform>()) {
            register(FirJvmTypeMapper::class, FirJvmTypeMapper(this))
        }
    }
}
