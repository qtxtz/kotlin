// RUN_PIPELINE_TILL: BACKEND
// ISSUE: KT-66570

fun testInference(container: IContainer<*, *>) {
    container.getProducer().<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>produce<!>
}

interface IContainer<out P : IProducer<T>, out T : IChild> {
    fun getProducer(): P
}

interface IProducer<out T : IParent> {
    val produce: T
}

interface IParent
interface IChild : IParent

/* GENERATED_FIR_TAGS: functionDeclaration, interfaceDeclaration, out, propertyDeclaration, starProjection,
typeConstraint, typeParameter */
