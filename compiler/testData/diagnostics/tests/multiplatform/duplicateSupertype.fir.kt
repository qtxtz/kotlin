// RUN_PIPELINE_TILL: BACKEND
// IGNORE_FIR_DIAGNOSTICS
// LATEST_LV_DIFFERENCE
// IGNORE_DEXING
// LANGUAGE: +MultiPlatformProjects
// ISSUE: KT-74221

// MODULE: common

interface A {
    open fun foo() {}
}

expect interface B

<!CONFLICTING_INHERITED_JVM_DECLARATIONS, CONFLICTING_INHERITED_JVM_DECLARATIONS, CONFLICTING_INHERITED_JVM_DECLARATIONS, CONFLICTING_INHERITED_JVM_DECLARATIONS!>class C : A, <!SUPERTYPE_APPEARS_TWICE!>B<!> {}<!>

// MODULE: jvm()()(common)

actual typealias B = A

/* GENERATED_FIR_TAGS: actual, classDeclaration, expect, functionDeclaration, interfaceDeclaration, typeAliasDeclaration */
