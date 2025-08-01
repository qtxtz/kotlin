// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-54473

interface I
class A : I
class B : I

fun test(a: A, b: B) {
    a == b
    a == b as I

    <!EQUALITY_NOT_APPLICABLE!>a === b<!>
    a === b as I
}

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, equalityExpression, functionDeclaration, interfaceDeclaration */
