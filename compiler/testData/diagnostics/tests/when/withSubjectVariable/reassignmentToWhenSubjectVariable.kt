// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// LANGUAGE: +VariableDeclarationInWhenSubject
// DIAGNOSTICS: -UNUSED_VARIABLE -UNUSED_PARAMETER -UNUSED_VALUE

fun foo(): Any = 42

fun test1(x: Any) {
    when (val y = foo()) {
        is String -> <!VAL_REASSIGNMENT!>y<!> = ""
    }
}

/* GENERATED_FIR_TAGS: assignment, functionDeclaration, integerLiteral, isExpression, localProperty, propertyDeclaration,
stringLiteral, whenExpression, whenWithSubject */
