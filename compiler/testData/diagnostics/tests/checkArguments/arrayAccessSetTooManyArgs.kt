// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER

class A {
    operator fun get(x: Int) {}
    operator fun set(x: String, value: Int) {}

    fun d(x: Int) {
        this["", 1] = <!TOO_MANY_ARGUMENTS!>1<!>
    }
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, functionDeclaration, integerLiteral, operator, stringLiteral,
thisExpression */
