// RUN_PIPELINE_TILL: FRONTEND
public fun foo(x: String?): Int {
    while (true) {
        // After the check, smart cast should work
        val y = if (x == null) break else <!DEBUG_INFO_SMARTCAST!>x<!>
        // y is not null in both branches
        y.length
    }
    // x is null because of the break
    return x<!UNSAFE_CALL!>.<!>length
}

/* GENERATED_FIR_TAGS: break, equalityExpression, functionDeclaration, ifExpression, localProperty, nullableType,
propertyDeclaration, smartcast, whileLoop */
