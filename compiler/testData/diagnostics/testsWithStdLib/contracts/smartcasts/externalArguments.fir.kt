// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: -UNUSED_VARIABLE

import kotlin.reflect.KProperty

fun testLambdaArgumentSmartCast(foo: Int?) {
    val v = run {
        if (foo != null)
            return@run foo
        15
    }
}

class D {
    operator fun getValue(ref: Any?, property: KProperty<*>): Int = 42
}

fun testSmartCastInDelegate(d: D?) {
    if (d == null) return
    val v: Int by d
}

fun testFunctionCallSmartcast(fn: (() -> Unit)?) {
    if (fn == null) return

    fn()
}

fun testCallableRefernceSmartCast() {
    fun forReference() {}

    val refernece = if (true) ::forReference else null
    if (refernece == null)
        return

    refernece()
}

/* GENERATED_FIR_TAGS: callableReference, classDeclaration, equalityExpression, functionDeclaration, functionalType,
ifExpression, integerLiteral, lambdaLiteral, localFunction, localProperty, nullableType, operator, propertyDeclaration,
propertyDelegate, smartcast, starProjection */
