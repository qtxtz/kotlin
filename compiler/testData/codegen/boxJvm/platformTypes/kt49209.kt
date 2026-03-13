// TARGET_BACKEND: JVM
// JVM_TARGET: 1.8
// FULL_JDK

// NCDF: java.util.Optional
// IGNORE_BACKEND: ANDROID

import java.util.Optional

fun <T> T?.toOptional(): Optional<T & Any> = Optional.ofNullable(this)

fun box(): String {
    return "OK".toOptional().get()
}