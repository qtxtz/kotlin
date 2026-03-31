/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.tests.compilation

import org.jetbrains.kotlin.buildtools.tests.CompilerExecutionStrategyConfiguration
import org.jetbrains.kotlin.buildtools.tests.compilation.assertions.assertLogContainsPatterns
import org.jetbrains.kotlin.buildtools.tests.compilation.model.BtaV2StrategyAgnosticCompilationTest
import org.jetbrains.kotlin.buildtools.tests.compilation.model.LogLevel
import org.jetbrains.kotlin.buildtools.tests.compilation.scenario.scenario
import org.jetbrains.kotlin.test.TestMetadata
import org.junit.jupiter.api.DisplayName

class CircularDependencyTest : BaseCompilationTest() {
    @BtaV2StrategyAgnosticCompilationTest
    @DisplayName("KT-29860 KT-55940 Circular dependency does not lead to infinite IC recursion")
    @TestMetadata("jvm-circular-dependency")
    fun circularDependency(strategyConfig: CompilerExecutionStrategyConfiguration) {
        scenario(strategyConfig) {
            val module = module("jvm-circular-dependency")
            // MONOTONOUS_INCREMENTAL_COMPILE_SET_EXPANSION is enabled by default

            module.replaceFileWithVersion("b.kt", "addCircularDependencyOnA")

            module.compile {
                expectFail()
                assertLogContainsPatterns(LogLevel.ERROR, ".*Type checking has run into a recursive problem.*".toRegex())
            }
        }
    }
}