// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: a.kt
package a

var X: Int = 1

// FILE: b.kt
package b

var X: String = ""

// FILE: c.kt
package c

import a.X
import b.*

fun foo() {
    X = 1
}

/* GENERATED_FIR_TAGS: assignment, functionDeclaration, integerLiteral, propertyDeclaration, stringLiteral */
