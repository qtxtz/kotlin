// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// FILE: a.kt
val <!REDECLARATION!>a<!> : Int = 1
<!CONFLICTING_OVERLOADS!>fun f()<!> {
}

// FILE: b.kt
val <!REDECLARATION!>a<!> : Int = 1
<!CONFLICTING_OVERLOADS!>fun f()<!> {
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, propertyDeclaration */
