// TARGET_BACKEND: JVM
// WITH_STDLIB
// FULL_JDK
// WORKS_WHEN_VALUE_CLASS
// LANGUAGE: +ContextParameters
// PARAMETERS_METADATA

package test

OPTIONAL_JVM_INLINE_ANNOTATION
value class A(val x: Int) {
    context(y: Int)
    fun f(regular: Int) = x + y + regular

    context(y: A)
    fun g(regular: Int) = this.x + y.x + regular
}

fun box(): String {
    checkInlineClass()

    return "OK"
}

private fun checkInlineClass() {
    val a = A(3)
    val sum1 = with(2) { a.f(100) }
    if (sum1 != 105) error(sum1.toString())
    val sum2 = with(a) { a.g(100) }
    if (sum2 != 106) error(sum2.toString())

    for (methodName in listOf("f-impl", "g-zj4m074")) {
        checkParameters("test.A", methodName)
    }
}

private fun checkParameters(className: String, methodName: String) {
    val methodNames = Class.forName(className).declaredMethods.map { it.name }
    if (methodName !in methodNames) {
        error("$methodName is not found in $methodNames")
    }
    val method = Class.forName(className).declaredMethods.single { it.name == methodName }
    val parameters = method.getParameters()
    for ((index, parameter) in parameters.withIndex()) {
        val isLast = index == parameters.lastIndex
        if (isLast == parameter.isSynthetic()) {
            error("wrong modifier on parameter $parameter of $methodName: ${parameter.modifiers}")
        }
    }
}
