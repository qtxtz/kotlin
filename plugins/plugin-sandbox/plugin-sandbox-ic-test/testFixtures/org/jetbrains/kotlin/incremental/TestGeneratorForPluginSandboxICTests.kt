/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.generators.dsl.junit4.generateTestGroupSuiteWithJUnit4
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5
import org.jetbrains.kotlin.test.TargetBackend

fun main(args: Array<String>) {
    generateTestGroupSuiteWithJUnit4(args) {
        testGroup("plugins/plugin-sandbox/plugin-sandbox-ic-test/tests-gen", "plugins/plugin-sandbox/plugin-sandbox-ic-test/testData/jvm") {
            testClass<AbstractIncrementalK2JvmWithPluginCompilerRunnerTest> {
                model("pureKotlin", extension = null, recursive = false, targetBackend = TargetBackend.JVM_IR)
            }
        }
    }
    generateTestGroupSuiteWithJUnit5(args) {
        testGroup("plugins/plugin-sandbox/plugin-sandbox-ic-test/tests-gen", "plugins/plugin-sandbox/plugin-sandbox-ic-test/testData/js") {
            testClass<AbstractIncrementalK2JsWithPluginSandboxPerModuleTest> {
                model("pureKotlin", recursive = false, pattern = "^([^_](.+))$")
            }
            testClass<AbstractIncrementalK2JsEs6WithPluginSandboxPerModuleTest> {
                model("pureKotlin", recursive = false, pattern = "^([^_](.+))$")
            }
            testClass<AbstractIncrementalK2JsWithPluginSandboxPerFileTest> {
                model("pureKotlin", recursive = false, pattern = "^([^_](.+))$")
            }
            testClass<AbstractIncrementalK2JsEs6WithPluginSandboxPerFileTest> {
                model("pureKotlin", recursive = false, pattern = "^([^_](.+))$")
            }
        }
    }
}
