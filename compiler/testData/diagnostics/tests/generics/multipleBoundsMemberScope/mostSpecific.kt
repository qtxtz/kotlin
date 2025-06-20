// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

interface A {
    fun foo(): CharSequence?
}

interface B {
    fun foo(): String
}

fun <T> test(x: T) where T : B, T : A {
    x.foo().checkType { _<String>() }
}

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration, functionalType, infix,
interfaceDeclaration, lambdaLiteral, nullableType, typeConstraint, typeParameter, typeWithExtension */
