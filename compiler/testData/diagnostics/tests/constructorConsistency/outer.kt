// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
class Outer {

    fun foo() = 1

    inner class Inner {

        val x = this@Outer.foo()

        val y = foo()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, inner, integerLiteral, propertyDeclaration, thisExpression */
