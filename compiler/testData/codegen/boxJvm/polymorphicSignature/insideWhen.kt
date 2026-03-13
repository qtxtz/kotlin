// TARGET_BACKEND: JVM
// WITH_STDLIB
// FULL_JDK

// MethodHandle.invoke and MethodHandle.invokeExact are only supported starting with Android O (--min-api 26)
// IGNORE_BACKEND: ANDROID

import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

object O {
    fun main() {}
}

fun f() = 2

fun box(): String {
    when (f()) {
        1 -> {}
        2 -> {
            val mh = MethodHandles.lookup().findVirtual(O::class.java, "main", MethodType.methodType(Void.TYPE))
            mh.invokeExact(O)
        }
        3 -> {}
        else -> {}
    }
    return "OK"
}
