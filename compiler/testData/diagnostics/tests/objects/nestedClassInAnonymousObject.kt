// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
class X {
    val foo = object {
        <!NESTED_CLASS_NOT_ALLOWED!>class Foo<!>
    }

    fun test() {
        object {
            <!NESTED_CLASS_NOT_ALLOWED!>class Foo<!>
        }
    }
}

/* GENERATED_FIR_TAGS: anonymousObjectExpression, classDeclaration, functionDeclaration, localClass, nestedClass,
propertyDeclaration */
