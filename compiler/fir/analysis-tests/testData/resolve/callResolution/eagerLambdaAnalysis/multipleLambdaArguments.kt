// RUN_PIPELINE_TILL: FRONTEND
// WITH_STDLIB
// ISSUE: KT-85593
// LANGUAGE: +EagerLambdaAnalysis

// Case 1: Named lambda + trailing lambda (from the issue)
@JvmName("a1Int")
fun a1(b: () -> Unit = {}, c: () -> Int): Int = 1
fun a1(b: () -> Unit = {}, c: () -> String): String = ""

// Case 2: Two positional lambdas
@JvmName("a2Int")
fun a2(b: () -> Unit, c: () -> Int): Int = 1
fun a2(b: () -> Unit, c: () -> String): String = ""

// Case 3: First lambda discriminates, trailing lambda is the same
@JvmName("a3Int")
fun a3(b: () -> Int, c: () -> Unit): Int = 1
fun a3(b: () -> String, c: () -> Unit): String = ""

// Case 4: Three lambda arguments, middle one discriminates
@JvmName("a4Int")
fun a4(a: () -> Unit, b: () -> Int, c: () -> Unit): Int = 1
fun a4(a: () -> Unit, b: () -> String, c: () -> Unit): String = ""

// Case 5: Generic type parameter on the first lambda, second lambda discriminates
@JvmName("a5Int")
fun <S> a5(b: () -> S, c: () -> Int): Int = 1
fun <S> a5(b: () -> S, c: () -> String): String = ""

// Case 6: Generic input types for the second lambda
@JvmName("a6Int")
fun <T> a6(b: () -> Unit, c: (T) -> Int): Int = 1
fun <T> a6(b: () -> Unit, c: (T) -> String): String = ""

fun main() {
    // Case 1
    val c1single = a1 { "" }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>c1single<!>

    val c1multi = <!OVERLOAD_RESOLUTION_AMBIGUITY!>a1<!>(b = { println("a") }) { "" }

    // Case 2
    val c2 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>a2<!>({ println("a") }) { "" }

    // Case 3: first lambda discriminates
    val c3 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>a3<!>({ "" }) { println("done") }

    // Case 4: three lambdas, middle one discriminates
    val c4 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>a4<!>({ println("a") }, { "" }) { println("done") }

    // Case 5: generic first lambda, second lambda discriminates
    val c5 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>a5<!>({ println("a") }) { "" }

    // Case 6: generic input types on the second lambda
    val c6 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>a6<!><Int>({ println("a") }) { "" }
}

/* GENERATED_FIR_TAGS: functionDeclaration, functionalType, integerLiteral, lambdaLiteral, localProperty, nullableType,
propertyDeclaration, stringLiteral, typeParameter */
