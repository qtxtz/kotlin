// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
fun <<!CYCLIC_GENERIC_UPPER_BOUND!>T : F?<!>, <!CYCLIC_GENERIC_UPPER_BOUND!>F : T?<!>> foo1() {}

fun <T : F?, <!CYCLIC_GENERIC_UPPER_BOUND!>F : E<!>, <!CYCLIC_GENERIC_UPPER_BOUND!>E : F?<!>> foo2() {}

fun <<!CYCLIC_GENERIC_UPPER_BOUND!>T<!>, <!CYCLIC_GENERIC_UPPER_BOUND!>F<!>> foo3() where T : F?, F : T {}

fun <T, <!CYCLIC_GENERIC_UPPER_BOUND!>F<!>, <!CYCLIC_GENERIC_UPPER_BOUND!>E<!>> foo4() where T : F?, F : E, E : F? {}

/* GENERATED_FIR_TAGS: functionDeclaration, nullableType, typeConstraint, typeParameter */
