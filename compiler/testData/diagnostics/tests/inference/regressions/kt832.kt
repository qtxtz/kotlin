// RUN_PIPELINE_TILL: FRONTEND
//KT-832 Provide better diagnostics when type inference fails for an expression that returns a function
package a

fun <T> fooT2() : (t : T) -> T {
    return {it}
}

fun test() {
    <!NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>fooT2<!>()(1) // here 1 should not be marked with an error
}

/* GENERATED_FIR_TAGS: functionDeclaration, functionalType, integerLiteral, lambdaLiteral, nullableType, typeParameter */
