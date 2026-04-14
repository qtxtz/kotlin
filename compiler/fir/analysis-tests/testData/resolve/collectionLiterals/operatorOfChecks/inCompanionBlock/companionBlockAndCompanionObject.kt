// LANGUAGE: +CompanionBlocksAndExtensions +CollectionLiterals
// RUN_PIPELINE_TILL: FRONTEND

class EmptyCompanionBlock {
    companion { }
    companion object {
        operator fun of(vararg x: String): EmptyCompanionBlock = EmptyCompanionBlock()
    }
}

class EmptyCompanionObject {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: String): EmptyCompanionObject = EmptyCompanionObject()
    }
    companion object
}

class TwoSetsOfSingleSame {
    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: Int): <!UNRESOLVED_REFERENCE!>TwoSetsOfSingle<!> = <!UNRESOLVED_REFERENCE!>TwoSetsOfSingle<!>()
    }

    companion object {
        operator fun of(vararg x: Int): TwoSetsOfSingleSame = TwoSetsOfSingleSame()
    }
}

class TwoSetsOfSingleDifferent {
    companion object {
        operator fun of(vararg x: Int): TwoSetsOfSingleDifferent = TwoSetsOfSingleDifferent()
    }

    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: Long): TwoSetsOfSingleDifferent = TwoSetsOfSingleDifferent()
    }
}

class TwoSetsOfMultiple {
    companion object {
        operator fun of(vararg x: Int): TwoSetsOfMultiple = TwoSetsOfMultiple()
        operator fun of(): TwoSetsOfMultiple = TwoSetsOfMultiple()
    }

    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(vararg x: Long): TwoSetsOfMultiple = TwoSetsOfMultiple()
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): TwoSetsOfMultiple = TwoSetsOfMultiple()
    }
}

class OneSetDistributed {
    companion object {
        operator fun of(vararg x: Int): OneSetDistributed = OneSetDistributed()
    }

    companion {
        <!INAPPLICABLE_OPERATOR_MODIFIER!>operator<!> fun of(): OneSetDistributed = OneSetDistributed()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, companionObject, functionDeclaration, objectDeclaration, operator, vararg */
