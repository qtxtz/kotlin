/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.expression

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.checkForLocalRedeclarations
import org.jetbrains.kotlin.fir.analysis.checkers.collectConflictingLocalFunctionsFrom
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.declarations.utils.isDestructuredParameter
import org.jetbrains.kotlin.fir.expressions.FirBlock

object FirConflictsExpressionChecker : FirBlockChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirBlock) {
        val elements =
            if (expression.statements.none { it.isDestructuredParameter() }) expression.statements // optimization
            else expression.statements.filterNot { it.isDestructuredParameter() }
        checkForLocalRedeclarations(elements)
        checkForLocalConflictingFunctions(expression)
    }

    context(context: CheckerContext, reporter: DiagnosticReporter)
    private fun checkForLocalConflictingFunctions(
        expression: FirBlock,
    ) {
        val inspector = collectConflictingLocalFunctionsFrom(expression) ?: return

        for ((function, otherFunctionsThatConflictWithIt) in inspector.declarationConflictingSymbols) {
            if (otherFunctionsThatConflictWithIt.isEmpty()) {
                continue
            }

            reporter.reportOn(function.source, FirErrors.CONFLICTING_OVERLOADS, otherFunctionsThatConflictWithIt)
        }

        for ((conflictingDeclaration, symbols) in inspector.declarationShadowedViaContextParameters) {
            if (symbols.isNotEmpty()) {
                reporter.reportOn(conflictingDeclaration.source, FirErrors.CONTEXTUAL_OVERLOAD_SHADOWED, symbols)
            }
        }
    }
}
