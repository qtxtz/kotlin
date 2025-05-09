/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.getModifierList
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.utils.modality
import org.jetbrains.kotlin.fir.declarations.utils.visibility
import org.jetbrains.kotlin.fir.symbols.impl.FirAnonymousObjectSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.lexer.KtTokens

object FirConstructorAllowedChecker : FirConstructorChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirConstructor) {
        val containingClass = context.containingDeclarations.lastOrNull() as? FirClassSymbol ?: return
        val source = declaration.source
        val elementType = source?.elementType
        if (elementType != KtNodeTypes.PRIMARY_CONSTRUCTOR && elementType != KtNodeTypes.SECONDARY_CONSTRUCTOR) {
            return
        }
        when (containingClass.classKind) {
            ClassKind.OBJECT -> reporter.reportOn(source, FirErrors.CONSTRUCTOR_IN_OBJECT)
            ClassKind.INTERFACE -> reporter.reportOn(source, FirErrors.CONSTRUCTOR_IN_INTERFACE)
            ClassKind.ENUM_ENTRY -> reporter.reportOn(source, FirErrors.CONSTRUCTOR_IN_OBJECT)
            ClassKind.ENUM_CLASS -> if (declaration.visibility != Visibilities.Private) {
                reporter.reportOn(source, FirErrors.NON_PRIVATE_CONSTRUCTOR_IN_ENUM)
            }
            ClassKind.CLASS -> when (containingClass) {
                is FirAnonymousObjectSymbol -> reporter.reportOn(source, FirErrors.CONSTRUCTOR_IN_OBJECT)
                is FirRegularClassSymbol -> if (containingClass.modality == Modality.SEALED) {
                    val modifierList = source.getModifierList() ?: return
                    val hasIllegalModifier = modifierList.modifiers.any {
                        val token = it.token
                        token in KtTokens.VISIBILITY_MODIFIERS && token != KtTokens.PROTECTED_KEYWORD && token != KtTokens.PRIVATE_KEYWORD
                    }
                    if (hasIllegalModifier) {
                        reporter.reportOn(source, FirErrors.NON_PRIVATE_OR_PROTECTED_CONSTRUCTOR_IN_SEALED)
                    }
                }
            }
            ClassKind.ANNOTATION_CLASS -> {
                // DO NOTHING
            }
        }
    }
}
