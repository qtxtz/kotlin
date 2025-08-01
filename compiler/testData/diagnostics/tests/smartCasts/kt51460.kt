// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// ISSUE: KT-51460

abstract class A {
    abstract protected val a: A?

    class B(override val a: A?) : A() {
        fun f(other: A) {
            val x = if (other is C) {
                other.a
            } else {
                null
            }
        }
    }

    class C(override val a: A?): A()
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, ifExpression, isExpression, localProperty, nestedClass,
nullableType, override, primaryConstructor, propertyDeclaration */
