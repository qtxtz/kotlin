// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

package s

import checkSubtype

interface In<in T>

interface A
interface B
interface C: A, B

fun <T> foo(in1: In<T>, in2: In<T>): T = throw Exception("$in1 $in2")

fun test(inA: In<A>, inB: In<B>, inC: In<C>) {

    foo(inA, inB)

    val r = foo(inA, inC)
    checkSubtype<C>(r)

    val c: C = foo(inA, inB)

    use(c)
}

fun <T: C> bar(in1: In<T>): T = throw Exception("$in1")

fun test(inA: In<A>) {
    val r = bar(inA)
    checkSubtype<C>(r)
}

fun use(vararg a: Any?) = a

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration, functionalType, in, infix,
interfaceDeclaration, intersectionType, localProperty, nullableType, outProjection, propertyDeclaration, stringLiteral,
typeConstraint, typeParameter, typeWithExtension, vararg */
