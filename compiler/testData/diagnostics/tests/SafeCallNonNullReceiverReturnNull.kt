// RUN_PIPELINE_TILL: FRONTEND
fun Int.gg() = null

fun ff() {
    val a: Int = 1
    val b: Int = <!TYPE_MISMATCH!>a<!UNNECESSARY_SAFE_CALL!>?.<!>gg()<!>
}

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, integerLiteral, localProperty, nullableType,
propertyDeclaration, safeCall */
