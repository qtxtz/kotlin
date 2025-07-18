// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER
@JvmName("bar")
fun foo(a: Any) {}

fun Any.foo() {}

@JvmName("barInt")
fun bar(x: List<Int>) {}

@JvmName("barStr")
fun bar(x: List<String>) {}

class C {
    var rwProp: Int
        @JvmName("get_rwProp")
        get() = 0
        @JvmName("set_rwProp")
        set(v) {}

    fun getRwProp(): Int = 123
    fun setRwProp(v: Int) {}
}

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration, getter, integerLiteral,
propertyDeclaration, setter, stringLiteral */
