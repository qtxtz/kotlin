/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.test.framework.projectStructure

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.analysis.api.platform.projectStructure.KaDanglingFileModuleImpl
import org.jetbrains.kotlin.analysis.api.projectStructure.KaDanglingFileResolutionMode
import org.jetbrains.kotlin.analysis.api.projectStructure.explicitModule
import org.jetbrains.kotlin.analysis.test.framework.services.TestForeignValue
import org.jetbrains.kotlin.analysis.test.framework.services.TestForeignValueProviderService
import org.jetbrains.kotlin.analysis.test.framework.services.expressionMarkerProvider
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.TestModuleKind
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isIdentifier
import org.jetbrains.kotlin.test.directives.model.DirectiveApplicability
import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer
import org.jetbrains.kotlin.test.directives.model.singleOrZeroValue
import org.jetbrains.kotlin.test.model.TestFile
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.sourceFileProvider
import org.jetbrains.org.objectweb.asm.Type
import java.nio.file.Path

/**
 * @see TestModuleKind.CodeFragment
 */
object KtCodeFragmentTestModuleFactory : KtTestModuleFactory {
    override fun createModule(
        testModule: TestModule,
        contextModule: KtTestModule?,
        dependencyBinaryRoots: Collection<Path>,
        testServices: TestServices,
        project: Project,
    ): KtTestModule {
        requireNotNull(contextModule) { "Code fragment requires a context module" }

        val testFile = testModule.files.singleOrNull() ?: error("A single file is expected for a code fragment module")

        val fileName = testFile.name
        val fileText = testServices.sourceFileProvider.getContentOfSourceFile(testFile)

        val codeFragmentKind = testFile.directives
            .singleOrZeroValue(AnalysisApiTestCodeFragmentDirectives.CODE_FRAGMENT_KIND)
            ?: CodeFragmentKind.BLOCK

        val codeFragmentImports = testFile
            .directives[AnalysisApiTestCodeFragmentDirectives.CODE_FRAGMENT_IMPORT]
            .joinToString(KtCodeFragment.IMPORT_SEPARATOR)
            .takeIf { it.isNotEmpty() }

        if (codeFragmentKind == CodeFragmentKind.TYPE && codeFragmentImports != null) {
            error("Imports cannot be configured for type code fragments")
        }

        val contextElement = contextModule.ktFiles
            .firstNotNullOfOrNull { findContextElement(it, testServices) }

        val codeFragment = when (codeFragmentKind) {
            CodeFragmentKind.EXPRESSION -> KtExpressionCodeFragment(project, fileName, fileText, codeFragmentImports, contextElement)
            CodeFragmentKind.BLOCK -> KtBlockCodeFragment(project, fileName, fileText, codeFragmentImports, contextElement)
            CodeFragmentKind.TYPE -> KtTypeCodeFragment(project, fileName, fileText, contextElement)
        }

        val foreignValues = testFile.directives[AnalysisApiTestCodeFragmentDirectives.CODE_FRAGMENT_FOREIGN_VALUE]
        TestForeignValueProviderService.submitForeignValues(codeFragment, foreignValues)


        val module = KaDanglingFileModuleImpl(
            listOf(codeFragment),
            contextModule.ktModule,
            getResolutionMode(testFile)
        )

        codeFragment.explicitModule = module

        return KtTestModule(TestModuleKind.CodeFragment, testModule, module, listOf(codeFragment))
    }

    private fun getResolutionMode(testFile: TestFile): KaDanglingFileResolutionMode {
        val directives = testFile.directives[AnalysisApiTestCodeFragmentDirectives.CODE_FRAGMENT_RESOLUTION_MODE]
        return when (directives.size) {
            0 -> KaDanglingFileResolutionMode.PREFER_SELF
            1 -> directives.single()
            else -> error("Expected 0 or 1 `${AnalysisApiTestCodeFragmentDirectives.CODE_FRAGMENT_RESOLUTION_MODE}` directives, got: ${directives.size}")
        }
    }

    private fun findContextElement(file: KtFile, testServices: TestServices): KtElement? {
        val offset = testServices.expressionMarkerProvider.getCaretOrNull(file, "context") ?: return null
        return file.findElementAt(offset)?.getParentOfType<KtElement>(strict = false)
    }
}

object AnalysisApiTestCodeFragmentDirectives : SimpleDirectivesContainer() {
    val CODE_FRAGMENT_KIND by enumDirective<CodeFragmentKind>(
        description = "Code fragment kind",
        applicability = DirectiveApplicability.File
    )

    val CODE_FRAGMENT_IMPORT by stringDirective(
        description = "Import local to the code fragment content",
        applicability = DirectiveApplicability.File
    )

    val CODE_FRAGMENT_RESOLUTION_MODE by enumDirective<KaDanglingFileResolutionMode>(
        description = "Import local to the code fragment content, default is `KaDanglingFileResolutionMode.PREFER_SELF`",
        applicability = DirectiveApplicability.File
    )


    val CODE_FRAGMENT_FOREIGN_VALUE by valueDirective<TestForeignValue>(
        description = "Value injected to a code fragment",
        applicability = DirectiveApplicability.File,
        parser = fun(rawText: String): TestForeignValue? {
            val match = CODE_FRAGMENT_FOREIGN_VALUE_REGEX.matchEntire(rawText) ?: return null
            val valueName = match.groupValues[1].also { require(it.isIdentifier()) }
            val valueType = match.groupValues[2].also { Type.getType(it) } // Check that it is a valid type descriptor
            return TestForeignValue(valueName, valueType)
        }
    )

    private val CODE_FRAGMENT_FOREIGN_VALUE_REGEX = Regex("^(.+)\\((.+)\\)$")
}

enum class CodeFragmentKind {
    EXPRESSION, BLOCK, TYPE
}