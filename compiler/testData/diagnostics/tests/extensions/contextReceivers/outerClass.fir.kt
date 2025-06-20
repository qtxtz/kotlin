// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -CONTEXT_RECEIVERS_DEPRECATED, -CONTEXT_CLASS_OR_CONSTRUCTOR
// LANGUAGE: +ContextReceivers
// DIAGNOSTICS: -UNUSED_PARAMETER

class Outer {
    val x: Int = 1
}

context(Outer)
class Inner(arg: Any) {
    fun bar() = x
}

fun f(outer: Outer) {
    <!NO_CONTEXT_ARGUMENT!>Inner<!>(1)
    with(outer) {
        Inner(3)
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, integerLiteral, lambdaLiteral, primaryConstructor,
propertyDeclaration */
