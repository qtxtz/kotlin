// RUN_PIPELINE_TILL: FRONTEND
class A<T, U : Any> {
    fun foo() = <!CALLABLE_REFERENCE_LHS_NOT_A_CLASS!>T::toString<!>

    fun bar() = <!CALLABLE_REFERENCE_LHS_NOT_A_CLASS!>U::toString<!>

    fun baz() {
        take(<!CALLABLE_REFERENCE_LHS_NOT_A_CLASS!>T::toString<!>)

        take(<!CALLABLE_REFERENCE_LHS_NOT_A_CLASS!>U::toString<!>)
    }
}

fun <T> foo() = <!CALLABLE_REFERENCE_LHS_NOT_A_CLASS!>T::toString<!>

fun <U : Any> bar() = <!CALLABLE_REFERENCE_LHS_NOT_A_CLASS!>U::toString<!>

fun take(arg: Any) {}

/* GENERATED_FIR_TAGS: callableReference, classDeclaration, functionDeclaration, nullableType, typeConstraint,
typeParameter */
