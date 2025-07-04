// RUN_PIPELINE_TILL: BACKEND
// DUMP_CFG

annotation class Ann

fun foo(b: Boolean) {
    if (b) {
        return
    }

    @Ann
    class Local()

    bar()
}

fun bar() {}

/* GENERATED_FIR_TAGS: annotationDeclaration, classDeclaration, functionDeclaration, ifExpression, localClass,
primaryConstructor */
