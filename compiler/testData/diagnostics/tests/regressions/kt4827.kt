// RUN_PIPELINE_TILL: FRONTEND
// KT-4827 UOE at PackageType.throwException()
// EA-53605

public interface TestInterface {
}

class C {
    inner class I {

    }
}

fun f() {
    <!RESOLUTION_TO_CLASSIFIER!>TestInterface<!>()
    C.<!RESOLUTION_TO_CLASSIFIER!>I<!>()
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, inner, interfaceDeclaration */
