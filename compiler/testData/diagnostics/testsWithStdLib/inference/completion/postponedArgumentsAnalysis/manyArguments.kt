// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_VARIABLE

fun main() {
    val list: List<Int.() -> Unit> = listOf({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})

    val map: Map<Int, Int.() -> Unit> = mapOf(
        1 to {},
        2 to {},
        3 to {},
        4 to {},
        5 to {},
        6 to {},
        7 to {},
        8 to {},
        9 to {},
        10 to {},
        11 to {},
        12 to {}
    )
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, lambdaLiteral, localProperty, propertyDeclaration */
