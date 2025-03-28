// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

data class A(val x: Int, val y: String)

fun foo(a: A) {
    val (b, c) = a
    checkSubtype<Int>(b)
    checkSubtype<String>(c)
}
