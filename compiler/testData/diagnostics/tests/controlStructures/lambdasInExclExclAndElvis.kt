// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_EXPRESSION

fun use(a: Any?) = a

fun test() {
    { }<!NOT_NULL_ASSERTION_ON_LAMBDA_EXPRESSION!>!!<!>
    use({ }<!NOT_NULL_ASSERTION_ON_LAMBDA_EXPRESSION!>!!<!>);

    // KT-KT-9070
    { } <!USELESS_ELVIS!>?: 1<!>
    use({ 2 } <!USELESS_ELVIS!>?: 1<!>);

    1 <!USELESS_ELVIS!>?: { }<!>
    use(1 <!USELESS_ELVIS!>?: { }<!>)
}

/* GENERATED_FIR_TAGS: checkNotNullCall, elvisExpression, functionDeclaration, integerLiteral, lambdaLiteral,
nullableType */
