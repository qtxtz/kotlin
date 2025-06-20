// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: Aaa.java
// http://youtrack.jetbrains.com/issue/KT-1694

public abstract class Aaa {
    public abstract void foo(String... args);
}

// FILE: bbb.kt

class Bbb() : Aaa() {
    override fun foo(vararg args: String?) = Unit
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, javaType, override, primaryConstructor, vararg */
