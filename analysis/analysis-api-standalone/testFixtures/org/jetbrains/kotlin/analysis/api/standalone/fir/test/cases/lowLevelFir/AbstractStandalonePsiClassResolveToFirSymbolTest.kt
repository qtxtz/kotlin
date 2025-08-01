/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.standalone.fir.test.cases.lowLevelFir

import org.jetbrains.kotlin.analysis.api.standalone.fir.test.configurators.StandaloneModeConfigurator
import org.jetbrains.kotlin.analysis.low.level.api.fir.AbstractPsiClassResolveToFirSymbolTest
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfigurator

abstract class AbstractStandalonePsiClassResolveToFirSymbolTest : AbstractPsiClassResolveToFirSymbolTest() {
    override val configurator: AnalysisApiTestConfigurator get() = StandaloneModeConfigurator
}
