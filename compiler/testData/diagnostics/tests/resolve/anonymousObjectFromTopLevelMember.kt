// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
interface IFoo {
    fun foo()
}

interface IBar

private fun createAnonObject() =
        object : IFoo, IBar {
            override fun foo() {}
            fun qux() {}
        }

private val propOfAnonObject = object : IFoo, IBar {
            override fun foo() {}
            fun qux() {}
        }

fun useAnonObject() {
    createAnonObject().foo()
    createAnonObject().qux()

    propOfAnonObject.foo()
    propOfAnonObject.qux()
}

/* GENERATED_FIR_TAGS: anonymousObjectExpression, functionDeclaration, interfaceDeclaration, override,
propertyDeclaration */
