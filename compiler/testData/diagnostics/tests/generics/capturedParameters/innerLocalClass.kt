// RUN_PIPELINE_TILL: FRONTEND
// CHECK_TYPE
// NI_EXPECTED_FILE
private class Outer<E> {
    private inner class Inner<out F> {
        private fun <G> foo() = {
            fun baz() = {
                class Local {
                    val e: E = magic()
                    val f: F = magic()
                    val g: G = magic()
                }
                Local()
            }
            baz()()
        }

        private var doubleCharSequenceInt = Outer<Double>().Inner<CharSequence>().foo<Int>()()
        private var doubleStringNumber = Outer<Double>().Inner<String>().foo<Number>()()
        private var doubleStringInt = Outer<Double>().Inner<String>().foo<Int>()()

        private fun bar() {
            doubleCharSequenceInt = <!TYPE_MISMATCH!>doubleStringNumber<!>
            doubleCharSequenceInt = doubleStringInt

            doubleStringInt = Outer<Double>().Inner<String>().foo<Int>()()

            doubleStringInt.e.checkType { _<Double>() }
            doubleStringInt.f.checkType { _<String>() }
            doubleStringInt.g.checkType { _<Int>() }
        }
    }
}

fun <T> magic(): T = null!!

/* GENERATED_FIR_TAGS: assignment, checkNotNullCall, classDeclaration, funWithExtensionReceiver, functionDeclaration,
functionalType, infix, inner, lambdaLiteral, localClass, localFunction, nullableType, out, propertyDeclaration,
typeParameter, typeWithExtension */
