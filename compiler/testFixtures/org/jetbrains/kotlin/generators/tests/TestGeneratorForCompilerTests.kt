/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.tests

import org.jetbrains.kotlin.asJava.AbstractCompilerLightClassTest
import org.jetbrains.kotlin.codegen.fir.*
import org.jetbrains.kotlin.codegen.ir.AbstractIrCheckLocalVariablesTableTest
import org.jetbrains.kotlin.codegen.ir.AbstractIrScriptCodegenTest
import org.jetbrains.kotlin.codegen.ir.AbstractIrWriteFlagsTest
import org.jetbrains.kotlin.codegen.ir.AbstractIrWriteSignatureTest
import org.jetbrains.kotlin.generators.dsl.junit4.generateTestGroupSuiteWithJUnit4
import org.jetbrains.kotlin.generators.util.TestGeneratorUtil
import org.jetbrains.kotlin.generators.util.TestGeneratorUtil.KT_OR_KTS_WITHOUT_DOTS_IN_NAME
import org.jetbrains.kotlin.jvm.compiler.AbstractLoadJava17Test
import org.jetbrains.kotlin.jvm.compiler.AbstractLoadJavaTest
import org.jetbrains.kotlin.jvm.compiler.fir.AbstractFirLightTreeCompileJavaAgainstKotlinTest
import org.jetbrains.kotlin.jvm.compiler.fir.AbstractFirPsiCompileJavaAgainstKotlinTest
import org.jetbrains.kotlin.jvm.compiler.ir.AbstractIrCompileJavaAgainstKotlinTest
import org.jetbrains.kotlin.jvm.compiler.ir.AbstractIrCompileKotlinWithJavacIntegrationTest
import org.jetbrains.kotlin.jvm.compiler.javac.AbstractLoadJavaUsingJavacTest
import org.jetbrains.kotlin.lexer.kdoc.AbstractKDocLexerTest
import org.jetbrains.kotlin.lexer.kotlin.AbstractKotlinLexerTest
import org.jetbrains.kotlin.modules.xml.AbstractModuleXmlParserTest
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.utils.CUSTOM_TEST_DATA_EXTENSION_PATTERN
import org.jetbrains.kotlin.types.AbstractTypeBindingTest

fun main(args: Array<String>) {
    val mainClassName = TestGeneratorUtil.getMainClassName()
    generateTestGroupSuiteWithJUnit4(args, mainClassName) {
        testGroup("compiler/tests-gen", "compiler/testData") {
            testClass<AbstractLoadJavaTest> {
                model("loadJava/compiledJava", extension = "java", testMethod = "doTestCompiledJava")
                model("loadJava/compiledJavaAndKotlin", extension = "txt", testMethod = "doTestCompiledJavaAndKotlin")
                model(
                    "loadJava/compiledJavaIncludeObjectMethods",
                    extension = "java",
                    testMethod = "doTestCompiledJavaIncludeObjectMethods"
                )
                model("loadJava/compiledKotlin", testMethod = "doTestCompiledKotlin")
                model("loadJava/compiledKotlinWithStdlib", testMethod = "doTestCompiledKotlinWithStdlib")
                model("loadJava/javaAgainstKotlin", extension = "txt", testMethod = "doTestJavaAgainstKotlin")
                model(
                    "loadJava/kotlinAgainstCompiledJavaWithKotlin",
                    extension = "kt",
                    testMethod = "doTestKotlinAgainstCompiledJavaWithKotlin",
                    recursive = false
                )
                model("loadJava/sourceJava", extension = "java", testMethod = "doTestSourceJava")
            }

            testClass<AbstractLoadJavaUsingJavacTest> {
                model("loadJava/compiledJava", extension = "java", testMethod = "doTestCompiledJava")
                model("loadJava/compiledJavaAndKotlin", extension = "txt", testMethod = "doTestCompiledJavaAndKotlin")
                model(
                    "loadJava/compiledJavaIncludeObjectMethods",
                    extension = "java",
                    testMethod = "doTestCompiledJavaIncludeObjectMethods"
                )
                model("loadJava/compiledKotlin", testMethod = "doTestCompiledKotlin")
                model("loadJava/compiledKotlinWithStdlib", testMethod = "doTestCompiledKotlinWithStdlib")
                model("loadJava/javaAgainstKotlin", extension = "txt", testMethod = "doTestJavaAgainstKotlin")
                model(
                    "loadJava/kotlinAgainstCompiledJavaWithKotlin",
                    extension = "kt",
                    testMethod = "doTestKotlinAgainstCompiledJavaWithKotlin",
                    recursive = false
                )
                model("loadJava/sourceJava", extension = "java", testMethod = "doTestSourceJava")
            }

            testClass<AbstractLoadJava17Test> {
                model("loadJava17", extension = "java", testMethod = "doTestCompiledJava", testClassName = "CompiledJava")
                model("loadJava17", extension = "java", testMethod = "doTestSourceJava", testClassName = "SourceJava")
            }

            testClass<AbstractModuleXmlParserTest> {
                model("modules.xml", extension = "xml")
            }

            testClass<AbstractCompilerLightClassTest> {
                model(
                    "asJava/lightClasses/lightClassByFqName",
                    excludeDirs = listOf("local", "ideRegression"),
                    pattern = KT_OR_KTS_WITHOUT_DOTS_IN_NAME,
                )
            }

            testClass<AbstractTypeBindingTest> {
                model("type/binding")
            }

            testClass<AbstractKDocLexerTest> {
                model("lexer/kdoc")
            }

            testClass<AbstractKotlinLexerTest> {
                model("lexer/kotlin")
            }

            testClass<AbstractIrCompileJavaAgainstKotlinTest> {
                model(
                    "compileJavaAgainstKotlin",
                    testClassName = "WithoutJavac",
                    testMethod = "doTestWithoutJavac",
                    targetBackend = TargetBackend.JVM_IR
                )
                model(
                    "compileJavaAgainstKotlin",
                    testClassName = "WithJavac",
                    testMethod = "doTestWithJavac",
                    targetBackend = TargetBackend.JVM_IR
                )
            }

            testClass<AbstractFirLightTreeCompileJavaAgainstKotlinTest> {
                model(
                    "compileJavaAgainstKotlin",
                    testClassName = "WithoutJavac",
                    testMethod = "doTestWithoutJavac",
                    targetBackend = TargetBackend.JVM_IR
                )
                model(
                    "compileJavaAgainstKotlin",
                    testClassName = "WithJavac",
                    testMethod = "doTestWithJavac",
                    targetBackend = TargetBackend.JVM_IR
                )
            }

            testClass<AbstractFirPsiCompileJavaAgainstKotlinTest> {
                model(
                    "compileJavaAgainstKotlin",
                    testClassName = "WithoutJavac",
                    testMethod = "doTestWithoutJavac",
                    targetBackend = TargetBackend.JVM_IR
                )
                model(
                    "compileJavaAgainstKotlin",
                    testClassName = "WithJavac",
                    testMethod = "doTestWithJavac",
                    targetBackend = TargetBackend.JVM_IR
                )
            }


            testClass<AbstractIrCompileKotlinWithJavacIntegrationTest> {
                model(
                    "compileKotlinAgainstJava",
                    testClassName = "WithAPT",
                    testMethod = "doTestWithAPT",
                    targetBackend = TargetBackend.JVM_IR
                )
                model(
                    "compileKotlinAgainstJava",
                    testClassName = "WithoutAPT",
                    testMethod = "doTestWithoutAPT",
                    targetBackend = TargetBackend.JVM_IR
                )
            }

            testClass<AbstractIrCheckLocalVariablesTableTest> {
                model("checkLocalVariablesTable", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractIrWriteFlagsTest> {
                model("writeFlags", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractIrWriteSignatureTest> {
                model("writeSignature", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractFirPsiCheckLocalVariablesTableTest> {
                model("checkLocalVariablesTable", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractFirPsiWriteFlagsTest> {
                model("writeFlags", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractFirPsiWriteSignatureTest> {
                model("writeSignature", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractFirLightTreeWriteFlagsTest> {
                model("writeFlags", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractFirLightTreeCheckLocalVariablesTableTest> {
                model("checkLocalVariablesTable", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractFirLightTreeWriteSignatureTest> {
                model("writeSignature", targetBackend = TargetBackend.JVM_IR)
            }

            testClass<AbstractIrScriptCodegenTest> {
                model("codegen/script", extension = "kts", targetBackend = TargetBackend.JVM_IR, excludedPattern = CUSTOM_TEST_DATA_EXTENSION_PATTERN)
            }
        }
    }
}
