// LANGUAGE: +CompanionBlocksAndExtensions +CollectionLiterals
// RUN_PIPELINE_TILL: FRONTEND

class A {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: Int): A = A()
    }
}

class B {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: Int): B = B()
    }
}

class C {
    companion object {
        operator fun of(vararg x: Int): C = C()
    }
}

fun Int.test() {
    val x = when (this) {
        0 -> A()
        1 -> B()
        else -> <!UNRESOLVED_REFERENCE!>[1, 2, 3]<!>
    }

    val y = when (this) {
        0 -> A()
        1 -> C()
        else -> [1, 2, 3]
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, companionObject, equalityExpression, funWithExtensionReceiver,
functionDeclaration, integerLiteral, localProperty, objectDeclaration, operator, propertyDeclaration, vararg,
whenExpression, whenWithSubject */
