// LANGUAGE: +CompanionBlocksAndExtensions +CollectionLiterals
// RUN_PIPELINE_TILL: FRONTEND

class SingleVararg {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg v: Int): SingleVararg = SingleVararg()
    }
}

class VarargAndOthers {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg v: Int): VarargAndOthers = VarargAndOthers()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): VarargAndOthers = VarargAndOthers()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(v: Int): VarargAndOthers = VarargAndOthers()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(a: Int, b: Int): VarargAndOthers = VarargAndOthers()
    }
}

class NonEmptyVararg {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(a: Int, vararg v: Int): NonEmptyVararg = NonEmptyVararg()
    }
}

class NonEmptyVarargAndEmpty {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): NonEmptyVarargAndEmpty = NonEmptyVarargAndEmpty()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(a: Int, vararg b: Int): NonEmptyVarargAndEmpty = NonEmptyVarargAndEmpty()
    }
}

class Generic<T> {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(): Generic<T> = Generic<T>()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(x: T): Generic<T> = Generic<T>()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(vararg x: T): Generic<T> = Generic<T>()
    }
}

class ImplicitReturnType<T> {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(vararg x: T) = ImplicitReturnType<T>()
    }
}

class InternalOf {
    companion {
        internal <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: String): InternalOf = InternalOf()
        internal <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): InternalOf = InternalOf()
    }
}

class InTwoBlocks {
    companion {
        private <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): InTwoBlocks = InTwoBlocks()
    }

    companion {
        private <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: String): InTwoBlocks = InTwoBlocks()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, nullableType, operator, typeParameter, vararg */
