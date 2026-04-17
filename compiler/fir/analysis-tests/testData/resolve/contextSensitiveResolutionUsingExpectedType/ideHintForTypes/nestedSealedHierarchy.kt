// RUN_PIPELINE_TILL: BACKEND
// IDE_MODE

// FILE: test.kt
package test

sealed class Outer {
    sealed class Mid1 : Outer() {
        class Leaf1 : Mid1()
        class Leaf2 : Mid1()
    }
    sealed class Mid2 : Outer() {
        class Leaf3 : Mid2()
        class Leaf4 : Mid2()
    }
}

// FILE: main.kt
import test.Outer

fun testOuter(o: Outer) {
    when (o) {
        is Outer.Mid1 -> {}
        is Outer.Mid2 -> {}
    }
}

fun testMid(m: Outer.Mid1) {
    when (m) {
        is Outer.Mid1.Leaf1 -> {}
        is Outer.Mid1.Leaf2 -> {}
    }
}

fun testCast(o: Outer) {
    val mid = o as Outer.Mid1
    val leaf = mid as? Outer.Mid1.Leaf1
}

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, functionDeclaration, isExpression, localProperty, nestedClass,
nullableType, propertyDeclaration, sealed, smartcast, whenExpression, whenWithSubject */
