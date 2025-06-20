// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +AllowContractsForCustomFunctions +UseCallsInPlaceEffect
// OPT_IN: kotlin.contracts.ExperimentalContracts
// DIAGNOSTICS: -INVISIBLE_REFERENCE -INVISIBLE_MEMBER

import kotlin.contracts.*

fun <T> runTwice(block: () -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }
    block()
    return block();
};

fun <T> funWithUnknownInvocations(block: () -> T) = block()

fun indefiniteFlow() {
    var x: Int

    funWithUnknownInvocations { runTwice { x = 42 } }

    <!UNINITIALIZED_VARIABLE!>x<!>.inc()
}

fun shadowing() {
    var x: Int
    runTwice { val x: Int; x = 42; x.inc() }
    <!UNINITIALIZED_VARIABLE!>x<!>.inc()
}

/* GENERATED_FIR_TAGS: assignment, contractCallsEffect, contracts, functionDeclaration, functionalType, integerLiteral,
lambdaLiteral, localProperty, nullableType, propertyDeclaration, typeParameter */
