// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +ModifierNonBuiltinSuspendFunError
// FIR_IDENTICAL
// SKIP_TXT

infix fun Int.suspend(c: () -> Unit) {}

fun foo() {
    1 <!MODIFIER_FORM_FOR_NON_BUILT_IN_SUSPEND_FUN_ERROR!>suspend<!> fun () {}

    1.suspend(fun () {})
}

/* GENERATED_FIR_TAGS: anonymousFunction, funWithExtensionReceiver, functionDeclaration, functionalType, infix,
integerLiteral */
