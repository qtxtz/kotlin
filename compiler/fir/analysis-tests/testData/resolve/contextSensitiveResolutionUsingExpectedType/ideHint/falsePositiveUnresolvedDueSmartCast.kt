// RUN_PIPELINE_TILL: FRONTEND
// WITH_STDLIB
// FULL_JDK
// IDE_MODE
// ISSUE: KT-85267

object Properties {
    val property: String? get() = null
}

fun <T> id(t: T): T = TODO()

val <F> F.myProp: String get() = ""

fun test() {
    if (Properties.property != null) {
        Properties.property?.length

        val x: String? = Properties.property

        id(Properties.<!UNRESOLVED_REFERENCE!>property<!>)
        Properties.property.myProp
        Properties.<!UNRESOLVED_REFERENCE!>property<!>!!
    }
}

/* GENERATED_FIR_TAGS: checkNotNullCall, equalityExpression, funWithExtensionReceiver, functionDeclaration, getter,
ifExpression, localProperty, nullableType, objectDeclaration, propertyDeclaration, safeCall, smartcast, typeParameter */
