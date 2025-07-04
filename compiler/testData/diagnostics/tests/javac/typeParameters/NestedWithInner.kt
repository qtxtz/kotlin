// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: a/x.java
package a;

public class x<T> {

    public static class Nested {

        public T getT() { return null; }

        public class T {

            public T getT() { return null; }

        }

    }

}

// FILE: test.kt
package test

import a.*

fun test() = x.Nested().getT().getT()

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, javaFunction, javaType */
