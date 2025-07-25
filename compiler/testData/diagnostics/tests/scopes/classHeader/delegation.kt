// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER

interface I

open class S(
        n: A.Nested,
        n2: A.Nested,
        inn: A.Inner,
        c: Int,
        cc: Int,
        cn: Int,
        ci: Int,
        t1: Int,
        t2: Int
) : I

class A : I by S(
        foo(),
        Nested(),
        <!RESOLUTION_TO_CLASSIFIER!>Inner<!>(),
        CONST,
        Companion.CONST,
        Nested.CONST,
        Interface.CONST,
        <!UNRESOLVED_REFERENCE!>a<!>,
        <!UNRESOLVED_REFERENCE!>b<!>()
) {

    class Nested {
        companion object {
            const val CONST = 2
        }
    }

    inner class Inner

    interface Interface {
        companion object {
            const val CONST = 3
        }
    }

    val a = 1
    fun b() = 2

    companion object {
        const val CONST = 1
        fun foo(): Nested = null!!
    }
}

/* GENERATED_FIR_TAGS: checkNotNullCall, classDeclaration, companionObject, const, functionDeclaration,
inheritanceDelegation, inner, integerLiteral, interfaceDeclaration, nestedClass, objectDeclaration, primaryConstructor,
propertyDeclaration */
