// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-58184

data class A(private val p1: Int, private val p2: Int)

fun test(a: A) {
    val (<!INVISIBLE_REFERENCE!>p1<!>, <!INVISIBLE_REFERENCE!>p2<!>) = a // ok, but INVISIBLE_MEMBER is expected
}

/* GENERATED_FIR_TAGS: classDeclaration, data, destructuringDeclaration, functionDeclaration, localProperty,
primaryConstructor, propertyDeclaration */
