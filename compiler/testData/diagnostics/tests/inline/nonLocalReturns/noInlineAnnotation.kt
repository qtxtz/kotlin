// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// DIAGNOSTICS: -NOTHING_TO_INLINE
fun main() {
    test {
        <!RETURN_NOT_ALLOWED!>return<!>
    }
}

inline fun test(noinline lambda: () -> Unit) {
    lambda()
}

/* GENERATED_FIR_TAGS: functionDeclaration, functionalType, inline, lambdaLiteral, noinline */
