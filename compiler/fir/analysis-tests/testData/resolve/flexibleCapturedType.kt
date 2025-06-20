// RUN_PIPELINE_TILL: BACKEND
// FILE: main.kt

fun foo(p: Processor<in CharSequence>, s: String?) {
    p.process(s)
}

// FILE: Processor.java
public interface Processor<T> {
    boolean process(T t);
}

/* GENERATED_FIR_TAGS: functionDeclaration, inProjection, javaType, nullableType */
