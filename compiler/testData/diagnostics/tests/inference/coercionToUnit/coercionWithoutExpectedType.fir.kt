// RUN_PIPELINE_TILL: FRONTEND
fun <T> materialize(): T = TODO()

fun implicitCoercion() {
    val a = {
        // Block is implicitly Unit-coerced, so it is allowed to place statement at the end of lambda
        if (true) 42
    }

    val b = l@{
        return@l
    }

    val c = l@{
        // Error: block doesn't have an expected type, so call can't be inferred!
        return@l <!CANNOT_INFER_PARAMETER_TYPE!>materialize<!>()
    }
}

/* GENERATED_FIR_TAGS: functionDeclaration, ifExpression, integerLiteral, lambdaLiteral, localProperty, nullableType,
propertyDeclaration, typeParameter */
