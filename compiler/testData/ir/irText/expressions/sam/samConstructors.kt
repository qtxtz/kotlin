// FIR_IDENTICAL
// WITH_JDK
// TARGET_BACKEND: JVM
fun test1() = Runnable { }

fun test2(a: () -> Unit) = Runnable(a)

fun foo() {}
fun test3() = Runnable(::foo)

fun test4() = Comparator<Int> { a, b -> a - b }