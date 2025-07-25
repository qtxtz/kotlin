// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

import kotlin.reflect.KFunction0

class A

class B {
    fun A.ext() {
        val x = ::A
        val y = ::B

        checkSubtype<KFunction0<A>>(x)
        checkSubtype<KFunction0<B>>(y)
    }
    
    fun B.ext() {
        val x = ::A
        val y = ::B

        checkSubtype<KFunction0<A>>(x)
        checkSubtype<KFunction0<B>>(y)
    }
}

/* GENERATED_FIR_TAGS: callableReference, classDeclaration, funWithExtensionReceiver, functionDeclaration,
functionalType, infix, localProperty, nullableType, propertyDeclaration, typeParameter, typeWithExtension */
