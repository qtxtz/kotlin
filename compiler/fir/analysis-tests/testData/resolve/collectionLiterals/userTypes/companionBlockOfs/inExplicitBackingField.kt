// LANGUAGE: +CollectionLiterals +CompanionBlocksAndExtensions
// RUN_PIPELINE_TILL: FRONTEND

class E<T>(val t: T) {
    companion {
        operator fun <T> of(vararg ts: T): E<T> = E(ts[0])
    }
}


class WithEbf {
    <!INCONSISTENT_BACKING_FIELD_TYPE!>val ebf: E<*><!>
        field = <!UNRESOLVED_REFERENCE!>[1, 2, 3]<!>

    fun member() {
        val asLong = ebf.t.<!UNRESOLVED_REFERENCE!>toLong<!>()
    }
}

/* GENERATED_FIR_TAGS: capturedType, classDeclaration, explicitBackingField, functionDeclaration, integerLiteral,
localProperty, nullableType, operator, outProjection, primaryConstructor, propertyDeclaration, starProjection,
typeParameter, vararg */
