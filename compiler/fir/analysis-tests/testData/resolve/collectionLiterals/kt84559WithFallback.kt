// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +CollectionLiterals
// WITH_STDLIB

@RequiresOptIn("", level = RequiresOptIn.Level.ERROR)
annotation class MyExperimental

@MyExperimental
object A

fun test() {
    val lst1 = @Unresolved [42]
    val lst2 = @OptIn(MyExperimental::class) <!OPT_IN_USAGE_ERROR!>[<!OPT_IN_USAGE_ERROR!>A<!>, <!OPT_IN_USAGE_ERROR!>A<!>]<!>
}

/* GENERATED_FIR_TAGS: annotationDeclaration, functionDeclaration, integerLiteral, localProperty, objectDeclaration,
propertyDeclaration, stringLiteral */
