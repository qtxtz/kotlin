// RUN_PIPELINE_TILL: BACKEND
// MODULE: m1-common
// FILE: common.kt

package test

expect fun foo(): String

fun g(f: () -> String): String = f()

fun test() {
    g(::<!DEPRECATION{JVM}!>foo<!>)
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

package test

@Deprecated("To check that ::foo is resolved to actual fun foo when compiling common+jvm")
actual fun foo(): String = ""

/* GENERATED_FIR_TAGS: actual, callableReference, expect, functionDeclaration, functionalType, stringLiteral */
