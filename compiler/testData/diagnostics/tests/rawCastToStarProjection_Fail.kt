// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// ISSUE: KT-57095

open class ValueType<T> {
    class Optional<T>: ValueType<T?>()
}
private fun checkType(type: ValueType<*>) {
    type as ValueType.Optional // K1: ok, K2: NO_TYPE_ARGUMENTS_ON_RHS
}

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, functionDeclaration, nestedClass, nullableType, starProjection,
typeParameter */
