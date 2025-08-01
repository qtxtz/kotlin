// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: -AllowDnnTypeOverridingFlexibleType
// FIR_IDENTICAL
// ISSUE: KT-58933
// FILE: J.java
public interface J<T> {
    void simple(T t);
    void box(Box<T> box);
}

// FILE: test.kt
class Box<T>

<!ABSTRACT_MEMBER_NOT_IMPLEMENTED!>class K<!><T> : J<T> {
    <!NOTHING_TO_OVERRIDE!>override<!> fun simple(t: T & Any) {}
    <!NOTHING_TO_OVERRIDE!>override<!> fun box(box: Box<T & Any>) {}
}

/* GENERATED_FIR_TAGS: classDeclaration, dnnType, functionDeclaration, javaType, nullableType, override, typeParameter */
