// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
fun f(x: Any) = x <!UNCHECKED_CAST!>as Array<String><!>

/* GENERATED_FIR_TAGS: asExpression, functionDeclaration */
