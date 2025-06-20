// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
interface IWithToString {
    override fun toString(): String
}

class A : IWithToString {
    // Should be Any#toString(), even though IWithToString defines an abstract toString.
    override fun toString(): String = super.toString()
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, override, superExpression */
