// DISABLE_JAVA_FACADE
// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: -UNUSED_VARIABLE -UNUSED_PARAMETER

// FILE: Foo.java

public class Foo {
    public static <K, V> void create(java.util.Map<? extends K, ? extends V> m) { return null; }
}

// FILE: test.kt

fun test(properties: Map<String, String>, nullableProperties: Map<String, String>?) {
    val f1 = Foo.create(select1(properties, myEmptyMap()))
    val f2 = Foo.create(nullableProperties ?: myEmptyMap())
}

fun <T, R> myEmptyMap(): Map<T, R> = TODO()

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER", "HIDDEN")
fun <S> select1(x: S, y: S): @kotlin.internal.Exact S = y

/* GENERATED_FIR_TAGS: elvisExpression, flexibleType, functionDeclaration, javaFunction, localProperty, nullableType,
outProjection, propertyDeclaration, stringLiteral, typeParameter */
