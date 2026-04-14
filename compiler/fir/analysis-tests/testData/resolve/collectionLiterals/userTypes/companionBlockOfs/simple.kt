// LANGUAGE: +CompanionBlocksAndExtensions +CollectionLiterals
// RUN_PIPELINE_TILL: FRONTEND

class MyList<T> {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(vararg x: T): MyList<T> = MyList()
    }
}

fun Int.expectedMyListInt(): MyList<Int> {
    when (this) {
        0 -> {
            return <!UNRESOLVED_REFERENCE!>[1, 2, 3]<!>
        }
        1 -> {
            return <!UNRESOLVED_REFERENCE!>[]<!>
        }
        else -> {
            return <!UNRESOLVED_REFERENCE!>["!"]<!>
        }
    }
}

fun test() {
    fun <T> accept(x: MyList<T>) { }

    <!CANNOT_INFER_PARAMETER_TYPE!>accept<!>(<!UNRESOLVED_REFERENCE!>[]<!>)
    <!CANNOT_INFER_PARAMETER_TYPE!>accept<!>(<!UNRESOLVED_REFERENCE!>[1, 2, 3]<!>)
    <!CANNOT_INFER_PARAMETER_TYPE!>accept<!>(<!UNRESOLVED_REFERENCE!>["!"]<!>)
    <!CANNOT_INFER_PARAMETER_TYPE!>accept<!>(<!UNRESOLVED_REFERENCE!>[1L, 2, 3.toByte()]<!>)

    accept<String>(<!UNRESOLVED_REFERENCE!>[]<!>)
    accept<String>(<!UNRESOLVED_REFERENCE!>[1, 2, 3]<!>)
    accept<String>(<!UNRESOLVED_REFERENCE!>["!"]<!>)

    val nullable: MyList<*>? = <!UNRESOLVED_REFERENCE!>[1, 2, 3]<!>
}

/* GENERATED_FIR_TAGS: classDeclaration, equalityExpression, funWithExtensionReceiver, functionDeclaration,
integerLiteral, localFunction, localProperty, nullableType, operator, propertyDeclaration, starProjection, stringLiteral,
typeParameter, vararg, whenExpression, whenWithSubject */
