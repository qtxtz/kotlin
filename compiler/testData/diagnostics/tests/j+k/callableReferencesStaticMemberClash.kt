// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER
// FILE: A.java
public class A {
    public static void foo() {}
    public static void foo(String x) {}

    private void foo(int y) {}
}

// FILE: main.kt

fun baz(x: (String) -> Unit) {}

fun bar() {
    baz(A::foo)
}

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, functionalType, javaCallableReference */
