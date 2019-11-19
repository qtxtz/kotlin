// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_PARAMETER

fun foo(x: Int, vararg y: String): String = y[0]

fun useArray(f: (Int, Array<String>) -> String): String = f(1, Array(1) { "OK" })

fun test(): String {
    return useArray(::foo)
}
