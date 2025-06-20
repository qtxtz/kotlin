// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_VARIABLE
// FILE: A.java

import java.util.*;

public class A {
    void foo(List<Object> x) {}
}
// FILE: main.kt

abstract class B : MutableList<String>

fun main(a: A, b: B) {
    a.foo(<!JAVA_TYPE_MISMATCH!>b<!>)
    a.foo(b as List<Any>)
}

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, functionDeclaration, javaFunction, javaType */
