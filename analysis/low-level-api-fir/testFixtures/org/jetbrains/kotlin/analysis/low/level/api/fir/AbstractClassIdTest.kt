/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.resolveToFirSymbolOfTypeSafe
import org.jetbrains.kotlin.analysis.low.level.api.fir.test.configurators.AnalysisApiFirScriptTestConfigurator
import org.jetbrains.kotlin.analysis.low.level.api.fir.test.configurators.AnalysisApiFirSourceTestConfigurator
import org.jetbrains.kotlin.analysis.test.framework.base.AbstractAnalysisApiBasedTest
import org.jetbrains.kotlin.analysis.test.framework.projectStructure.KtTestModule
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfigurator
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.ClassIdBasedLocality
import org.jetbrains.kotlin.psi.KtClassLikeDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions

abstract class AbstractClassIdTest : AbstractAnalysisApiBasedTest() {
    override fun doTestByMainFile(mainFile: KtFile, mainModule: KtTestModule, testServices: TestServices) {
        val text = buildString {
            withResolutionFacade(mainFile) { resolutionFacade ->
                mainFile.accept(object : PsiElementVisitor() {
                    override fun visitElement(element: PsiElement) {
                        if (element is KtClassLikeDeclaration) {
                            val psiClassId = element.getClassId()
                            val firSymbolResult = runCatching {
                                element.resolveToFirSymbolOfTypeSafe<FirClassLikeSymbol<*>>(resolutionFacade)
                            }

                            val error = if (firSymbolResult.isFailure) {
                                val exception = firSymbolResult.exceptionOrNull()!!
                                "(${exception::class.simpleName}: ${exception.message})"
                            } else {
                                val firClassId = firSymbolResult.getOrNull()?.classId?.takeUnless {
                                    @OptIn(ClassIdBasedLocality::class)
                                    it.isLocal
                                }

                                if (psiClassId == firClassId) null else "(FirClassId: $firClassId)"
                            }

                            append("/* ClassId: $psiClassId ${if (error != null) "$error " else ""}*/")
                        }

                        if (element is LeafPsiElement) {
                            append(element.text)
                        }

                        element.acceptChildren(this)
                    }

                    override fun visitComment(comment: PsiComment) {
                        if (comment.tokenType == KtTokens.BLOCK_COMMENT) {
                            return
                        }

                        super.visitComment(comment)
                    }
                })
            }
        }

        testServices.assertions.assertEqualsToTestOutputFile(text, mainFile.name.substringAfterLast('.'))
    }
}

abstract class AbstractSourceClassIdTest : AbstractClassIdTest() {
    override val configurator: AnalysisApiTestConfigurator = AnalysisApiFirSourceTestConfigurator(analyseInDependentSession = false)
}

abstract class AbstractScriptClassIdTest : AbstractClassIdTest() {
    override val configurator: AnalysisApiTestConfigurator = AnalysisApiFirScriptTestConfigurator(analyseInDependentSession = false)
}