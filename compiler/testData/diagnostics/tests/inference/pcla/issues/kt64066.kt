// RUN_PIPELINE_TILL: BACKEND
// WITH_STDLIB
// ISSUE: KT-64066

fun box() {
    val map = buildMap {
        put(1, 1)
        for (<!BUILDER_INFERENCE_STUB_PARAMETER_TYPE!>v<!> in values) {}
    }
}

/* GENERATED_FIR_TAGS: forLoop, functionDeclaration, integerLiteral, lambdaLiteral, localProperty, nullableType,
propertyDeclaration */
