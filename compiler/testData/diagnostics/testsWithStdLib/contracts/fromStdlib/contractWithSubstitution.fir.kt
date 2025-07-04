// RUN_PIPELINE_TILL: FRONTEND
// ISSUES: KT-57911, KT-56744
// LANGUAGE: -AllowCheckForErasedTypesInContracts

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

abstract class Base<T> {
    @OptIn(ExperimentalContracts::class)
    fun checkNotNull(s: String?) {
        contract { returns() implies (s != null) }
        s!!
    }

    @OptIn(ExperimentalContracts::class)
    fun checkIsT(s: Any?): Boolean {
        contract { <!ERROR_IN_CONTRACT_DESCRIPTION!>returns(true) implies (s is <!CANNOT_CHECK_FOR_ERASED!>T<!>)<!> }
        return false
    }

    @OptIn(ExperimentalContracts::class)
    fun <R> checkIsOwnerR(s: Any?): Boolean {
        contract { <!ERROR_IN_CONTRACT_DESCRIPTION!>returns(true) implies (s is <!CANNOT_CHECK_FOR_ERASED!>R<!>)<!> }
        return false
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <reified R> checkIsReifiedR(s: Any?): Boolean {
        contract { returns(true) implies (s is R) }
        return false
    }

    open fun foo(s: String?) {
        checkNotNull(s)
        s.length
    }
}

class Derived: Base<String>() {
    override fun foo(s: String?) {
        checkNotNull(s)
        s.length
    }

    fun test_1(s: Any) {
        if (checkIsT(s)) {
            s.<!UNRESOLVED_REFERENCE!>length<!>
        }
    }

    fun test_2(s: Any) {
        if (checkIsOwnerR<String>(s)) {
            s.<!UNRESOLVED_REFERENCE!>length<!>
        }
    }

    fun test_3(s: Any) {
        if (checkIsReifiedR<String>(s)) {
            s.length
        }
    }
}

fun test_1(d: Derived, s: String?) {
    d.checkNotNull(s)
    s.length
}

fun test_2(d: Derived, s: Any?) {
    if (d.checkIsT(s)) {
        s.<!UNRESOLVED_REFERENCE!>length<!>
    }
}

fun test_3(d: Derived, s: Any?) {
    if (d.checkIsOwnerR<String>(s)) {
        s.<!UNRESOLVED_REFERENCE!>length<!>
    }
}

fun test_4(d: Derived, s: Any?) {
    if (d.checkIsReifiedR<String>(s)) {
        s.length
    }
}

/* GENERATED_FIR_TAGS: checkNotNullCall, classDeclaration, classReference, contractConditionalEffect, contracts,
equalityExpression, functionDeclaration, ifExpression, inline, isExpression, lambdaLiteral, nullableType, override,
reified, smartcast, typeParameter */
