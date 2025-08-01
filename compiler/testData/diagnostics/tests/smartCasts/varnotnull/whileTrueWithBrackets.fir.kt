// RUN_PIPELINE_TILL: FRONTEND

fun x(): Boolean { return true }

public fun foo(pp: String?): Int {
    var p = pp
    while(true) {
        p!!.length
        if (x()) break
        <!WRAPPED_LHS_IN_ASSIGNMENT_ERROR!>(p)<!> = null
    }
    // Smart cast is NOT possible here
    // (we could provide it but p = null makes it much harder)
    return p.length
}

/* GENERATED_FIR_TAGS: assignment, break, checkNotNullCall, functionDeclaration, ifExpression, localProperty,
nullableType, propertyDeclaration, smartcast, whileLoop */
