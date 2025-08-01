// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER

import kotlin.reflect.KProperty

class StringDelegate(val s: String) {
    operator fun getValue(a: Any?, p: KProperty<*>): Int = 42
}

// NB no operator
fun String.provideDelegate(a: Any?, p: KProperty<*>) = StringDelegate(this)

operator fun String.getValue(a: Any?, p: KProperty<*>) = this

val test1: String by "OK"
val test2: Int <!DELEGATE_SPECIAL_FUNCTION_RETURN_TYPE_MISMATCH!>by<!> "OK"
val test3 by "OK"

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration, integerLiteral, nullableType,
operator, primaryConstructor, propertyDeclaration, propertyDelegate, starProjection, stringLiteral, thisExpression */
