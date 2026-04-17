// RUN_PIPELINE_TILL: BACKEND
// IDE_MODE

// FILE: test.kt
package test

sealed class A {
    class X(val v: String = "") : A()
    class Y(val v: String = "") : A()
}

// FILE: main.kt
import test.A

fun foo(a: A) {
    val x = a as A.X
    val y = a <!CAST_NEVER_SUCCEEDS!>as?<!> A.Y
    x.v
    y?.v
}

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, functionDeclaration, localProperty, nestedClass, nullableType,
primaryConstructor, propertyDeclaration, safeCall, sealed, smartcast, stringLiteral */
