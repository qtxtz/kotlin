// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER

fun test(a: Int, b: Boolean) {
    bar(a.foo(<!TYPE_MISMATCH!>b<!>))
}

fun <T, R> T.foo(l: (T) -> R): R = TODO()

fun <S> bar(a: S) {}

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, functionalType, nullableType, typeParameter */
