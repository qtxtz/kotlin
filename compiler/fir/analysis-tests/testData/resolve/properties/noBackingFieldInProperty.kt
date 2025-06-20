// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-41977

class A {
    val field: String = "" // (1)

    val x
        get() = field.length // should be ok, resolve to (1)
}

class B {
    val field: String = ""

    <!MUST_BE_INITIALIZED!>val x: Int<!>
        get() = field.<!UNRESOLVED_REFERENCE!>length<!> // should be an error
}

/* GENERATED_FIR_TAGS: classDeclaration, getter, propertyDeclaration, stringLiteral */
