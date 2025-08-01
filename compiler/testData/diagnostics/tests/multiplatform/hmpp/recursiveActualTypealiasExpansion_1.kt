// IGNORE_FIR_DIAGNOSTICS
// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-69632
// MODULE: common
expect <!EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE{JVM;JVM}!>class A<!>
expect <!EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE{JVM}!>class B<!>

expect fun commonFun<!NO_ACTUAL_FOR_EXPECT{JVM}!>(a: <!RECURSIVE_TYPEALIAS_EXPANSION{JVM;JVM}!>A<!>)<!>

// MODULE: intermediate1()()(common)
actual typealias <!EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE, EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE{JVM}!>A<!> = <!RECURSIVE_TYPEALIAS_EXPANSION{JVM;JVM}!>B<!>

// MODULE: intermediate2()()(intermediate1)
actual typealias <!EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE!>B<!> = <!RECURSIVE_TYPEALIAS_EXPANSION, RECURSIVE_TYPEALIAS_EXPANSION{JVM}!>A<!>

// MODULE: main()()(intermediate2)
actual fun commonFun<!ACTUAL_WITHOUT_EXPECT!>(a: <!RECURSIVE_TYPEALIAS_EXPANSION!>A<!>)<!> {}

fun test() { <!UNRESOLVED_REFERENCE!>A<!>(); <!UNRESOLVED_REFERENCE!>B<!>(); }

/* GENERATED_FIR_TAGS: actual, classDeclaration, expect, functionDeclaration, typeAliasDeclaration */
