// TARGET_BACKEND: JVM
// WITH_STDLIB
// FULL_JDK

// INDY not implemented on minSdkVersion 23
// IGNORE_BACKEND: ANDROID

class MyMap : MutableMap<Int, String> by hashMapOf()

fun box(): String {
    val map = MyMap()
    return map.computeIfAbsent(42) { "OK" }
}
