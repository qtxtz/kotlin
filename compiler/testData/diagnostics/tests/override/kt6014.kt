// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
interface IBase {
    override fun toString(): String
}

interface IDerived : IBase

object BaseImpl : IBase {
    override fun toString(): String = "A"
}

object DerivedImpl : IDerived {
    override fun toString(): String = "A"
}

class Test1 : IBase by BaseImpl

class Test2 : IDerived by DerivedImpl

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, inheritanceDelegation, interfaceDeclaration,
objectDeclaration, override, stringLiteral */
