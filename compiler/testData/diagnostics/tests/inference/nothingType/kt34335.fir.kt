// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER, -DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE

fun call(vararg x: Any?) {}
fun <R> Any.call(vararg args: Any?): R = TODO()
fun println(message: Any?) {}

fun foo(action: (Int) -> Unit) {
    action(10)
}

fun test1() {
    call({ <!CANNOT_INFER_VALUE_PARAMETER_TYPE!>x<!> -> println(x::class) }) // x inside the lambda is inferred to `Nothing`, the lambda is `(Nothing) -> Unit`.
}

fun test2() {
    ::foo.<!CANNOT_INFER_PARAMETER_TYPE!>call<!>({ <!CANNOT_INFER_VALUE_PARAMETER_TYPE!>x<!> -> println(x::class) })
}

/* GENERATED_FIR_TAGS: callableReference, classReference, funWithExtensionReceiver, functionDeclaration, functionalType,
integerLiteral, lambdaLiteral, nullableType, typeParameter, vararg */
