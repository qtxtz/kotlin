// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-60305
// NI_EXPECTED_FILE

class C<T>

typealias CStar = C<*>
typealias CIn = C<in Int>
typealias COut = C<out Int>
typealias CT<T> = C<T>

val test1 = <!CONSTRUCTOR_OR_SUPERTYPE_ON_TYPEALIAS_WITH_TYPE_PROJECTION_ERROR!>CStar()<!>
val test2 = <!CONSTRUCTOR_OR_SUPERTYPE_ON_TYPEALIAS_WITH_TYPE_PROJECTION_ERROR!>CIn()<!>
val test3 = <!CONSTRUCTOR_OR_SUPERTYPE_ON_TYPEALIAS_WITH_TYPE_PROJECTION_ERROR!>COut()<!>
val test4 = CT<<!PROJECTION_ON_NON_CLASS_TYPE_ARGUMENT!>*<!>>()
val test5 = CT<CT<*>>()

/* GENERATED_FIR_TAGS: capturedType, classDeclaration, inProjection, nullableType, outProjection, propertyDeclaration,
starProjection, typeAliasDeclaration, typeAliasDeclarationWithTypeParameter, typeParameter */
