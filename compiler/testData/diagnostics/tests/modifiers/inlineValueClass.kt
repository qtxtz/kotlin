// FIR_IDENTICAL
// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -INLINE_CLASS_DEPRECATED
// WITH_STDLIB

@JvmInline
<!INCOMPATIBLE_MODIFIERS!>inline<!> <!INCOMPATIBLE_MODIFIERS!>value<!> class A(val x: Int)

@JvmInline
<!INCOMPATIBLE_MODIFIERS!>value<!> <!INCOMPATIBLE_MODIFIERS!>inline<!> class B(val x: Int)

/* GENERATED_FIR_TAGS: classDeclaration, primaryConstructor, propertyDeclaration, value */
