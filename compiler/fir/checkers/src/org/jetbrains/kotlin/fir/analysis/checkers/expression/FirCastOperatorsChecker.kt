/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.expression

import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticWithSource
import org.jetbrains.kotlin.diagnostics.chooseFactory
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.*
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.analysis.diagnostics.createOn
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.analysis.checkers.firPlatformSpecificCastChecker
import org.jetbrains.kotlin.fir.isEnabled
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.types.*

object FirCastOperatorsChecker : FirTypeOperatorCallChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirTypeOperatorCall) {
        val arguments = expression.argumentList.arguments
        require(arguments.size == 1) { "Type operator call with non-1 arguments" }

        val l = arguments[0].toArgumentInfo()
        val r = expression.conversionTypeRef.coneType
            .fullyExpandedType()
            .finalApproximationOrSelf()

        if (expression.operation in FirOperation.TYPES && r is ConeDynamicType) {
            reporter.reportOn(expression.conversionTypeRef.source, FirErrors.DYNAMIC_NOT_ALLOWED)
        }

        val rUserType = expression.conversionTypeRef.coneType.finalApproximationOrSelf()

        when (val it = context.session.firPlatformSpecificCastChecker.runApplicabilityCheck(expression, l.originalType, r, this)) {
            Applicability.APPLICABLE -> {}
            // CAST_ERASED may not be the case if we factor in the smartcast data.
            Applicability.CAST_ERASED if l.argument is FirSmartCastExpression -> {}
            // An upcast may be useful for "canceling" a smartcast.
            Applicability.USELESS_CAST if l.argument is FirSmartCastExpression -> {}
            else -> return reporter.reportInapplicabilityDiagnostic(expression, it, l, r, rUserType)
        }

        if (l.argument !is FirSmartCastExpression) {
            return
        }

        context.session.firPlatformSpecificCastChecker.runApplicabilityCheck(
            expression,
            l.smartCastType,
            r,
            this
        ).ifInapplicable {
            return reporter.reportInapplicabilityDiagnostic(expression, it, l, r, rUserType, forceWarning = true)
        }
    }

    // checks applicability with no platform-specific rules
    context(context: CheckerContext)
    fun checkGeneralApplicability(expression: FirTypeOperatorCall, l: TypeInfo, r: TypeInfo) : Applicability = when (expression.operation) {
        FirOperation.IS, FirOperation.NOT_IS -> checkIsApplicability(l, r, expression)
        FirOperation.AS, FirOperation.SAFE_AS -> checkAsApplicability(l, r, expression)
        else -> error("Invalid operator of FirTypeOperatorCall")
    }

    context(context: CheckerContext)
    private fun checkIsApplicability(l: TypeInfo, r: TypeInfo, expression: FirTypeOperatorCall): Applicability = checkCastErased(l, r)
        .orIfApplicable {
            checkAnyApplicability(
                l, r, expression,
                Applicability.IMPOSSIBLE_IS_CHECK,
                Applicability.USELESS_IS_CHECK,
            )
        }

    context(context: CheckerContext)
    private fun checkAsApplicability(l: TypeInfo, r: TypeInfo, expression: FirTypeOperatorCall): Applicability {
        val isNullableNothingWithNotNull = !l.type.isMarkedOrFlexiblyNullable && r.type.isNullableNothing
                || l.type.isNullableNothing && !r.type.isMarkedOrFlexiblyNullable

        return when {
            l.type.isNothing -> Applicability.APPLICABLE
            r.type.isNothing -> Applicability.IMPOSSIBLE_CAST
            isNullableNothingWithNotNull -> when (expression.operation) {
                // (null as? WhatEver) == null
                FirOperation.SAFE_AS -> Applicability.USELESS_CAST
                else -> Applicability.IMPOSSIBLE_CAST
            }
            // For `as`-casts, `CAST_ERASED` is an error and is more important, whereas
            // for `is`-checks, usually, diagnostics for useless checks are more useful.
            else -> checkAnyApplicability(
                l, r, expression,
                Applicability.IMPOSSIBLE_CAST,
                Applicability.USELESS_CAST,
            ).orIfApplicable { checkCastErased(l, r) }
        }
    }

    context(context: CheckerContext)
    private fun checkCastErased(l: TypeInfo, r: TypeInfo): Applicability = when {
        !(context.isContractBody
                && LanguageFeature.AllowCheckForErasedTypesInContracts.isEnabled()
                ) && isCastErased(l.directType, r.directType) -> {
            Applicability.CAST_ERASED
        }
        else -> Applicability.APPLICABLE
    }

    context(context: CheckerContext)
    private fun checkAnyApplicability(
        l: TypeInfo, r: TypeInfo,
        expression: FirTypeOperatorCall,
        impossible: Applicability,
        useless: Applicability,
    ): Applicability = when {
        isRefinementUseless(l.directType.upperBoundIfFlexible(), r.directType, expression) -> useless
        shouldReportAsPerRules1(l, r) -> impossible
        else -> Applicability.APPLICABLE
    }

    /**
     * K1 reports different diagnostics for different
     * cases, and this enum helps to replicate the K1's
     * choice of diagnostics.
     *
     * Should the K2's diagnostic severity differ,
     * the proper version will be picked later
     * when reporting the diagnostic.
     */
    enum class Applicability {
        APPLICABLE,
        IMPOSSIBLE_CAST,
        IMPOSSIBLE_IS_CHECK,
        USELESS_CAST,
        USELESS_IS_CHECK,
        CAST_ERASED,
    }

    inline fun Applicability.ifInapplicable(block: (Applicability) -> Unit) {
        when (this) {
            Applicability.APPLICABLE -> {}
            else -> block(this)
        }
    }

    private inline fun Applicability.orIfApplicable(other: () -> Applicability): Applicability {
        return when {
            this == Applicability.APPLICABLE -> other()
            else -> this
        }
    }


    context(context: CheckerContext)
    private fun DiagnosticReporter.reportInapplicabilityDiagnostic(
        expression: FirTypeOperatorCall,
        applicability: Applicability,
        l: ArgumentInfo,
        r: ConeKotlinType,
        rUserType: ConeKotlinType,
        forceWarning: Boolean = false,
    ) {
        val areBothTypesNullable = l.smartCastType.canBeNull(context.session) && r.canBeNull(context.session)

        when (applicability) {
            Applicability.IMPOSSIBLE_CAST -> getImpossibilityDiagnostic(areBothTypesNullable, expression)?.let {
                reportOn(expression.source, it)
            }
            Applicability.USELESS_CAST -> getUselessCastDiagnostic()?.let {
                reportOn(expression.source, it)
            }
            Applicability.IMPOSSIBLE_IS_CHECK -> report(
                getImpossibleIsCheckDiagnostic(forceWarning, areBothTypesNullable, expression),
                context,
            )
            Applicability.USELESS_IS_CHECK -> when {
                !isLastBranchOfExhaustiveWhen(l) -> reportOn(
                    expression.source,
                    FirErrors.USELESS_IS_CHECK,
                    expression.operation == FirOperation.IS
                )
            }
            Applicability.CAST_ERASED -> when {
                expression.operation == FirOperation.AS || expression.operation == FirOperation.SAFE_AS -> {
                    reportOn(expression.source, FirErrors.UNCHECKED_CAST, l.userType, rUserType)
                }
                else -> reportOn(expression.conversionTypeRef.source, FirErrors.CANNOT_CHECK_FOR_ERASED, rUserType)
            }
            else -> error("Shouldn't be here")
        }
    }

    context(context: CheckerContext)
    private fun isLastBranchOfExhaustiveWhen(l: ArgumentInfo): Boolean {
        if (context.containingElements.size < 2) {
            return false
        }

        val (whenExpression, whenBranch) = context.containingElements.dropLast(1).takeLast(2)

        return whenExpression is FirWhenExpression && whenBranch is FirWhenBranch
                && whenExpression.isExhaustive && whenBranch == whenExpression.branches.lastOrNull()
                // Ensures it's not redundantly exhaustive
                && !l.argument.resolvedType.isNothing
                // Having an exhaustive `when` with only one branch is useless in general
                && whenExpression.branches.size > 1
    }

    context(context: CheckerContext)
    private fun getImpossibilityDiagnostic(
        areBothTypesNullable: Boolean,
        expression: FirTypeOperatorCall,
    ) = when {
        !LanguageFeature.EnableDfaWarningsInK2.isEnabled() -> null
        areBothTypesNullable -> when (expression.operation) {
            FirOperation.SAFE_AS -> FirErrors.SAFE_CAST_RELYING_ON_NULL
            else -> FirErrors.UNSAFE_CAST_RELYING_ON_NULL
        }
        else -> FirErrors.CAST_NEVER_SUCCEEDS
    }

    context(context: CheckerContext)
    private fun getUselessCastDiagnostic() = when {
        !LanguageFeature.EnableDfaWarningsInK2.isEnabled() -> null
        else -> FirErrors.USELESS_CAST
    }

    context(context: CheckerContext)
    private fun getImpossibleIsCheckDiagnostic(
        forceWarning: Boolean,
        areBothTypesNullable: Boolean,
        expression: FirTypeOperatorCall,
    ): KtDiagnosticWithSource? {
        val isAlwaysTrue = expression.operation != FirOperation.IS

        val (factoryForDeprecation, valueToWarnAbout) = when {
            areBothTypesNullable -> FirErrors.IMPOSSIBLE_IS_CHECK_RELYING_ON_NULL to !isAlwaysTrue
            else -> FirErrors.IMPOSSIBLE_IS_CHECK to isAlwaysTrue
        }

        val factory = when {
            forceWarning -> factoryForDeprecation.warningFactory
            else -> factoryForDeprecation.chooseFactory(context)
        }

        return factory.createOn(expression.source, valueToWarnAbout, context.session)
    }
}
