// RUN_PIPELINE_TILL: BACKEND
// ISSUE: KT-18055

fun main() {
    data class Stat(val link: String? = null)

    var stat = Stat()

    if (stat.link != null) {
        takeString(<!SMARTCAST_IMPOSSIBLE!>stat.link<!>)
    }
}

fun takeString(link: String) {}

/* GENERATED_FIR_TAGS: classDeclaration, data, equalityExpression, functionDeclaration, ifExpression, localClass,
localProperty, nullableType, primaryConstructor, propertyDeclaration, smartcast */
