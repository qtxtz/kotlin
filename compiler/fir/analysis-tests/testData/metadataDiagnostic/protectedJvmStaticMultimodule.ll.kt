// LL_FIR_DIVERGENCE
// Metadata checkers in the Analysis API are not yet correctly set up. See KT-82220.
// LL_FIR_DIVERGENCE

// LANGUAGE: +MultiPlatformProjects
// ISSUE: KT-85021
// WITH_STDLIB
// DIAGNOSTICS: -NOTHING_TO_INLINE
// MODULE: m1-common
// FILE: m1.kt

import kotlin.jvm.JvmStatic

abstract class Parent {
    companion object {
        @JvmStatic
        protected val someString = "OK"
    }
}

// MODULE: m2-common(m1-common)
// FILE: m2.kt
import kotlin.jvm.JvmName

class Child : Parent() {
    protected inline fun foo() = someString
}

@JvmName("<123>")
fun bar() {}
