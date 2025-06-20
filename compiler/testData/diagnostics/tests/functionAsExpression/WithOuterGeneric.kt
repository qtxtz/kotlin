// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER -UNUSED_VARIABLE

fun <T> parameter() = fun (t: T) = t
fun <T> receiver() = fun T.() = this
fun <T> returnType() = fun (): T = null!!

val <T> T.fromVal: () -> T get() = fun (): T = this@fromVal

fun devNull(a: Any?){}

fun <O> outer() {
    fun <T> parameter() = fun (t: T) = t
    fun <T> receiver() = fun T.() = this
    fun <T> returnType() = fun (): T = null!!

    devNull(fun (t: O) = t)
    devNull(fun O.() = this)
    devNull(fun (): O = null!!)
}

class Outer<O> {
    fun <T> parameter() = fun (t: T) = t
    fun <T> receiver() = fun T.() = this
    fun <T> returnType() = fun (): T = null!!

    init {
        devNull(fun (t: O) = t)
        devNull(fun O.() = this)
        devNull(fun (): O = null!!)
    }
}

/* GENERATED_FIR_TAGS: anonymousFunction, checkNotNullCall, classDeclaration, functionDeclaration, functionalType,
getter, init, localFunction, nullableType, propertyDeclaration, propertyWithExtensionReceiver, thisExpression,
typeParameter */
