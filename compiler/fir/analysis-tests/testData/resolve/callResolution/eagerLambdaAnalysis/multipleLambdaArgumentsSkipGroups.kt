// RUN_PIPELINE_TILL: FRONTEND
// WITH_STDLIB
// ISSUE: KT-85593
// LANGUAGE: +EagerLambdaAnalysis

// Case 1: Non-function expected type (Any parameter) — first group skipped, second discriminates
@JvmName("c1Int")
fun c1(a: Any, b: () -> Int): Int = 1
fun c1(a: () -> Unit, b: () -> String): String = ""

// Case 2: Different parameter count — first group skipped, second discriminates
@JvmName("c2NoParam")
fun c2(a: () -> Unit, b: () -> Int): Int = 1
fun c2(a: (Int) -> Unit, b: () -> String): String = ""

// Case 3: Different input types — first group skipped, second discriminates
@JvmName("c3Int")
fun c3(a: (Int) -> Unit, b: () -> Int): Int = 1
fun c3(a: (String) -> Unit, b: () -> String): String = ""

fun main() {
    // Case 1: first group has null expectedType (Any) -> skip, second group resolves
    val x1 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>c1<!>({}, { "" })

    // Case 2: first group has different param count (0 vs 1) -> skip, second group resolves
    val x2 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>c2<!>({}, { "" })

    // Case 3: first group has different input types (Int vs String) -> skip, second group resolves
    val x3 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>c3<!>({ <!CANNOT_INFER_IT_PARAMETER_TYPE!>_<!> -> }, { "" })
}

/* GENERATED_FIR_TAGS: functionDeclaration, functionalType, integerLiteral, lambdaLiteral, localProperty,
propertyDeclaration, stringLiteral */
