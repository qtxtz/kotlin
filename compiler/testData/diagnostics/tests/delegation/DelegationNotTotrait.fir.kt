// RUN_PIPELINE_TILL: FRONTEND
open class Foo() {

}

class Barrr() : <!DELEGATION_NOT_TO_INTERFACE, SUPERTYPE_NOT_INITIALIZED!>Foo<!> by Foo() {}

interface T {}

class Br(t : T) : T by t {}

<!WRONG_MODIFIER_TARGET!>open<!> enum class EN() {
  A
}

class Test2(e : EN) : <!DELEGATION_NOT_TO_INTERFACE!>EN<!> by e {}

/* GENERATED_FIR_TAGS: classDeclaration, enumDeclaration, enumEntry, inheritanceDelegation, interfaceDeclaration,
primaryConstructor */
