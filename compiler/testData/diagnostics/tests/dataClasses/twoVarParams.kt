// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

data class A(var x: Int, var y: String)

fun foo(a: A) {
    checkSubtype<Int>(a.component1())
    checkSubtype<String>(a.component2())
}

/* GENERATED_FIR_TAGS: classDeclaration, data, funWithExtensionReceiver, functionDeclaration, functionalType, infix,
nullableType, primaryConstructor, propertyDeclaration, typeParameter, typeWithExtension */
