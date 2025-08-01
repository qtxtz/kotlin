// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-68613

interface Generic<out T>

typealias TA<K> = (String) -> Generic<K>

fun test(it: Any) {
    val that = it <!UNCHECKED_CAST!>as Generic<TA<<!REDUNDANT_PROJECTION!>out<!> Any>><!>
}

fun rest(it: Any) {
    val that = it <!UNCHECKED_CAST!>as Generic<TA<<!CONFLICTING_PROJECTION_IN_TYPEALIAS_EXPANSION!>in<!> Any>><!>
}

/* GENERATED_FIR_TAGS: asExpression, functionDeclaration, functionalType, inProjection, interfaceDeclaration,
localProperty, nullableType, out, outProjection, propertyDeclaration, typeAliasDeclaration,
typeAliasDeclarationWithTypeParameter, typeParameter */
