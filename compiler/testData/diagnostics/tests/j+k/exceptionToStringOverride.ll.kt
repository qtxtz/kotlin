// LL_FIR_DIVERGENCE
// KT-70896
// LL_FIR_DIVERGENCE
// FIR_IDENTICAL
// RUN_PIPELINE_TILL: BACKEND

// FILE: Ex.java
class Ex extends Exception {
    @Override
    public String toString() {
        return super.toString();
    }
}

// FILE: usage.kt
private fun foo(ex: Ex) {
    ex.<!DEPRECATION!>toString<!>()
}

class Ex2 : Exception() {
    override fun toString(): String {
        return super.<!DEPRECATION!>toString<!>()
    }
}

/* GENERATED_FIR_TAGS: functionDeclaration, javaFunction, javaType */
