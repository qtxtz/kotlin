// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// SKIP_TXT
// FILE: A.java
public class A {
    public String getS4ClassRepresentation() { return ""; }
}

// FILE: main.kt
fun foo(a: A) {
    a.s4ClassRepresentation.length
}

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, javaProperty, javaType */
