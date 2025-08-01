// LL_FIR_DIVERGENCE
// Not a real LL divergence, it's just tiered runners reporting errors from `BACKEND`
// LL_FIR_DIVERGENCE
// RUN_PIPELINE_TILL: BACKEND
// This test is for the case when expect annotation is FirAnnotationCall and actual annotation is not FirAnnotationCall
// MODULE: common
expect annotation class Ann(val p: Int)

@Ann(p = 1)
expect class Foo

// MODULE: main()()(common)
// FILE: Foo.kt
actual annotation class Ann actual constructor(actual val p: Int)

actual typealias <!ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT!>Foo<!> = FooImpl

// FILE: FooImpl.java
@Ann(p = 2)
public class FooImpl {
}

/* GENERATED_FIR_TAGS: actual, annotationDeclaration, classDeclaration, expect, integerLiteral, javaType,
primaryConstructor, propertyDeclaration, typeAliasDeclaration */
