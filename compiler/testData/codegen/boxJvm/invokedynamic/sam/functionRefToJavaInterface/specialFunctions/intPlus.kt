// TARGET_BACKEND: JVM
// INDY not implemented on minSdkVersion 23
// IGNORE_BACKEND: ANDROID
// WITH_STDLIB
// JVM_TARGET: 1.8
// FULL_JDK

fun box(): String {
    val map = HashMap<String, Int>()
    map["a"] = 1
    map.merge("a", 2, Int::plus)
    val t = map["a"]
    return if (t == 3) "OK" else "failed: t=$t"
}
