// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: test/JavaClass.java

package test;

public class JavaClass {
    protected static int field;

    protected static String method() {
        return "";
    }
}

// FILE: test.kt

package test

fun test() {
    JavaClass.field
    JavaClass.method()
}

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, javaFunction, javaProperty */
