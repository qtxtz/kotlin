// RUN_PIPELINE_TILL: BACKEND
interface A {
    fun foo()
}

fun Any.test() {
    if (this is A) {
        val a = this
        a.foo()
    }
}


