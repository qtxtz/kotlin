/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.tests.compilation

import org.jetbrains.kotlin.build.report.metrics.BuildAttribute
import org.jetbrains.kotlin.buildtools.api.ExecutionPolicy
import org.jetbrains.kotlin.buildtools.api.SourcesChanges
import org.jetbrains.kotlin.buildtools.api.arguments.CommonToolArguments
import org.jetbrains.kotlin.buildtools.api.arguments.ExperimentalCompilerArgument
import org.jetbrains.kotlin.buildtools.api.arguments.JvmCompilerArguments
import org.jetbrains.kotlin.buildtools.api.arguments.enums.JvmTarget
import org.jetbrains.kotlin.buildtools.api.jvm.JvmSnapshotBasedIncrementalCompilationConfiguration.Companion.KEEP_IC_CACHES_IN_MEMORY
import org.jetbrains.kotlin.buildtools.api.jvm.JvmSnapshotBasedIncrementalCompilationConfiguration.Companion.TRACK_CONFIGURATION_INPUTS
import org.jetbrains.kotlin.buildtools.api.jvm.JvmSnapshotBasedIncrementalCompilationConfiguration.Companion.UNSAFE_INCREMENTAL_COMPILATION_FOR_MULTIPLATFORM
import org.jetbrains.kotlin.buildtools.tests.CompilerExecutionStrategyConfiguration
import org.jetbrains.kotlin.buildtools.tests.compilation.assertions.assertLogContainsPatterns
import org.jetbrains.kotlin.buildtools.tests.compilation.assertions.assertLogDoesNotContainPatterns
import org.jetbrains.kotlin.buildtools.tests.compilation.model.BtaV2StrategyAgnosticCompilationTest
import org.jetbrains.kotlin.buildtools.tests.compilation.model.LogLevel
import org.jetbrains.kotlin.buildtools.tests.compilation.model.project
import org.jetbrains.kotlin.test.TestMetadata

class ConfigurationInputsTrackingTest : BaseCompilationTest() {

    @BtaV2StrategyAgnosticCompilationTest
    @TestMetadata("jvm-module-1")
    fun testFirstBuildForcesNonIncrementalRebuild(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project(strategyConfig) {
            val module = module("jvm-module-1")
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = { it[TRACK_CONFIGURATION_INPUTS] = true },
            ) {
                assertLogContainsPatterns(
                    expectedLogLevel(strategyConfig),
                    ".*Non-incremental compilation will be performed: ${BuildAttribute.UNKNOWN_CHANGES_IN_GRADLE_INPUTS.readableString}".toRegex(),
                )
            }
        }
    }

    @BtaV2StrategyAgnosticCompilationTest
    @TestMetadata("jvm-module-1")
    fun testSubsequentBuildWithUnchangedConfigStaysIncremental(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project(strategyConfig) {
            val module = module("jvm-module-1")
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = { it[TRACK_CONFIGURATION_INPUTS] = true },
            )
            module.compileIncrementally(
                SourcesChanges.Known(modifiedFiles = emptyList(), removedFiles = emptyList()),
                icOptionsConfigAction = { it[TRACK_CONFIGURATION_INPUTS] = true },
            ) {
                assertLogContainsPatterns(LogLevel.DEBUG, ".*Incremental compilation completed".toRegex())
                assertLogDoesNotContainPatterns(
                    expectedLogLevel(strategyConfig),
                    ".*Non-incremental compilation will be performed.*".toRegex(),
                )
            }
        }
    }

    @BtaV2StrategyAgnosticCompilationTest
    @TestMetadata("jvm-module-1")
    fun testCompilerArgsChangeTriggersNonIncrementalRebuild(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project(strategyConfig) {
            val module = module("jvm-module-1")
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = { it[TRACK_CONFIGURATION_INPUTS] = true },
            )
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = { it[TRACK_CONFIGURATION_INPUTS] = true },
                compilationConfigAction = { it.compilerArguments[JvmCompilerArguments.JVM_TARGET] = JvmTarget.JVM_11 },
            ) {
                assertLogContainsPatterns(
                    expectedLogLevel(strategyConfig),
                    ".*Non-incremental compilation will be performed: ${BuildAttribute.COMPILER_ARGS_CHANGED.readableString}".toRegex(),
                )
            }
        }
    }

    @BtaV2StrategyAgnosticCompilationTest
    @TestMetadata("jvm-module-1")
    fun testCompilerArgsChangeDoesNotTriggerNonIncrementalRebuild(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project(strategyConfig) {
            val module = module("jvm-module-1")
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = { it[TRACK_CONFIGURATION_INPUTS] = true },
                compilationConfigAction = { it.compilerArguments[CommonToolArguments.VERSION] = false },
            )
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = { it[TRACK_CONFIGURATION_INPUTS] = true },
                compilationConfigAction = { it.compilerArguments[CommonToolArguments.VERSION] = true },
            ) {
                assertLogContainsPatterns(LogLevel.DEBUG, ".*Incremental compilation completed".toRegex())
                assertLogDoesNotContainPatterns(
                    expectedLogLevel(strategyConfig),
                    ".*Non-incremental compilation will be performed.*".toRegex(),
                )
            }
        }
    }

    @OptIn(ExperimentalCompilerArgument::class)
    @BtaV2StrategyAgnosticCompilationTest
    @TestMetadata("jvm-module-1")
    fun testIcConfigChangeTriggersNonIncrementalRebuild(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project(strategyConfig) {
            val module = module("jvm-module-1")
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = {
                    it[TRACK_CONFIGURATION_INPUTS] = true
                    it[UNSAFE_INCREMENTAL_COMPILATION_FOR_MULTIPLATFORM] = true
                },
            )
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = {
                    it[TRACK_CONFIGURATION_INPUTS] = true
                    it[UNSAFE_INCREMENTAL_COMPILATION_FOR_MULTIPLATFORM] = false
                },
            ) {
                assertLogContainsPatterns(
                    expectedLogLevel(strategyConfig),
                    ".*Non-incremental compilation will be performed: ${BuildAttribute.INCREMENTAL_COMPILATION_CONFIGURATION_CHANGED.readableString}".toRegex(),
                )
            }
        }
    }

    @BtaV2StrategyAgnosticCompilationTest
    @TestMetadata("jvm-module-1")
    fun testIcConfigChangeDoesNotTriggerNonIncrementalRebuild(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project(strategyConfig) {
            val module = module("jvm-module-1")
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = {
                    it[TRACK_CONFIGURATION_INPUTS] = true
                    it[KEEP_IC_CACHES_IN_MEMORY] = true
                },
            )
            module.compileIncrementally(
                SourcesChanges.ToBeCalculated,
                icOptionsConfigAction = {
                    it[TRACK_CONFIGURATION_INPUTS] = true
                    it[KEEP_IC_CACHES_IN_MEMORY] = false
                },
            ) {
                assertLogContainsPatterns(LogLevel.DEBUG, ".*Incremental compilation completed".toRegex())
                assertLogDoesNotContainPatterns(
                    expectedLogLevel(strategyConfig),
                    ".*Non-incremental compilation will be performed.*".toRegex(),
                )
            }
        }
    }

    private fun expectedLogLevel(strategyConfig: CompilerExecutionStrategyConfiguration): LogLevel =
        if (strategyConfig.second is ExecutionPolicy.WithDaemon) LogLevel.DEBUG else LogLevel.INFO // TODO: KT-85024
}
