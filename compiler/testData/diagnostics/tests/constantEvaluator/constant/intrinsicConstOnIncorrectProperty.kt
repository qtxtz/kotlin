// FIR_IDENTICAL
// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +IntrinsicConstEvaluation
@Deprecated("Deprecated in Java".abc)
fun test() {}

fun String.abc() : String = this

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, stringLiteral, thisExpression */
