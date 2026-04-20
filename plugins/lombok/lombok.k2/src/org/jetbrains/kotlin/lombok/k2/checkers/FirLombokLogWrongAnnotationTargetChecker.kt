/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.lombok.k2.checkers

import org.jetbrains.kotlin.descriptors.annotations.KotlinTarget
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirBasicDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.checkers.getActualTargetList
import org.jetbrains.kotlin.fir.analysis.checkers.getAllowedAnnotationTargets
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.lombok.utils.LombokNames

object FirLombokLogWrongAnnotationTargetChecker : FirBasicDeclarationChecker(MppCheckerKind.Common) {
    private val narrowedAllowedAnnotationTargets = listOf(
        KotlinTarget.CLASS_ONLY,
        KotlinTarget.OBJECT,
        KotlinTarget.ENUM_CLASS,
    );

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirDeclaration) {
        val logAnnotation = declaration.getAnnotationByClassId(LombokNames.LOG_ID, context.session) ?: return

        val defaultTargets = getActualTargetList(declaration).defaultTargets

        if (defaultTargets.none { narrowedAllowedAnnotationTargets.contains(it) }) {
            // Filter out diagnostics reported by a regular annotations checker to get rid of duplicates
            val allowedAnnotationTargets = logAnnotation.getAllowedAnnotationTargets(context.session)
            if (defaultTargets.any { allowedAnnotationTargets.contains(it) }) {
                reporter.reportOn(
                    logAnnotation.source,
                    FirErrors.WRONG_ANNOTATION_TARGET,
                    defaultTargets.firstOrNull()?.description ?: "unidentified target",
                    narrowedAllowedAnnotationTargets,
                )
            }
        }
    }
}
