// LANGUAGE: +CompanionBlocksAndExtensions +CollectionLiterals
// RUN_PIPELINE_TILL: FRONTEND

class NoVararg {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): NoVararg = NoVararg()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(x: Int): NoVararg = NoVararg()
    }
}

class TwoVarargs {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: String): TwoVarargs = TwoVarargs()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: Char): TwoVarargs = TwoVarargs()
    }
}

class TypeParameterMismatch<out T> {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): TypeParameterMismatch<Nothing> = TypeParameterMismatch()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(vararg t: T): TypeParameterMismatch<T> = TypeParameterMismatch()
    }
}

class ReturnTypeMismatch<out T> {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(): ReturnTypeMismatch<Nothing> = ReturnTypeMismatch()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(vararg t: T): ReturnTypeMismatch<T> = ReturnTypeMismatch()
    }
}

class VisibilityMismatch {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): VisibilityMismatch = VisibilityMismatch()
        internal <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg i: Int): VisibilityMismatch = VisibilityMismatch()
    }
}

class NullableReturn {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg i: Int): NullableReturn? = null
    }
}

class UnrelatedReturn {
    class Unrelated { }
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg i: Int): Unrelated = Unrelated()
    }
}

class ParameterMismatch {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg i: Int): ParameterMismatch = ParameterMismatch()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(i: Long): ParameterMismatch = ParameterMismatch()
    }
}

class SameParameterMismatch {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(l: Long, vararg i: Int): SameParameterMismatch = SameParameterMismatch()
    }
}

class BoundMismatch<T> {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(vararg i: T): BoundMismatch<T> = BoundMismatch()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T: Any> of(): BoundMismatch<T> = BoundMismatch()
    }
}

class ReifiednessMismatch {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <T> of(vararg i: T): ReifiednessMismatch = ReifiednessMismatch()
        inline <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun <reified T> of(): ReifiednessMismatch = ReifiednessMismatch()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, inline, nestedClass, nullableType, operator, out,
primaryConstructor, reified, typeConstraint, typeParameter, vararg */
