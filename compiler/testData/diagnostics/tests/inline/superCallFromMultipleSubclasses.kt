// RUN_PIPELINE_TILL: FRONTEND
// FILE: 1.kt

package test

open class A {
    open fun test(s: String) = s
}

object B : A() {
    override fun test(s: String) = "fail"

    <!NOTHING_TO_INLINE!>inline<!> fun doTest(s: String) = <!SUPER_CALL_FROM_PUBLIC_INLINE_ERROR!>super.test(s)<!>
}

object C : A() {
    override fun test(s: String) = "fail"

    <!NOTHING_TO_INLINE!>inline<!> fun doTest(s: String) = <!SUPER_CALL_FROM_PUBLIC_INLINE_ERROR!>super.test(s)<!>
}

// FILE: 2.kt

import test.*

fun box(): String {
    return B.doTest("O") + C.doTest("K")
}

/* GENERATED_FIR_TAGS: additiveExpression, classDeclaration, functionDeclaration, inline, objectDeclaration, override,
stringLiteral, superExpression */
