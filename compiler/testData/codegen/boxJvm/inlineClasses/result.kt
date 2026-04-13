// TARGET_BACKEND: JVM
// kotlin package is being relocated in Android tests
// IGNORE_BACKEND: ANDROID
// ALLOW_KOTLIN_PACKAGE
// WITH_STDLIB
// WORKS_WHEN_VALUE_CLASS
// LANGUAGE: +JvmInlineMultiFieldValueClasses

// FILE: result.kt
package kotlin

OPTIONAL_JVM_INLINE_ANNOTATION
value class Result(val value: Any?)

// FILE: box.kt

fun box(): String {
    return Result("OK").value as String
}
