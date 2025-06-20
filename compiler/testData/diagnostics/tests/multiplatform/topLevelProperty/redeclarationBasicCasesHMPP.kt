// IGNORE_FIR_DIAGNOSTICS
// RUN_PIPELINE_TILL: FIR2IR
// LANGUAGE: +MultiPlatformProjects

// MODULE: common
expect val <!REDECLARATION{JVM;JVM}!>x1<!>: Int

expect val <!AMBIGUOUS_ACTUALS{JVM}, EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE{JVM}!>x2<!>: Int

expect val <!AMBIGUOUS_ACTUALS{JVM}, EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE{JVM}!>x3<!>: Int

// MODULE: intermediate()()(common)
expect val <!REDECLARATION, REDECLARATION{JVM}!>x1<!>: Int

val <!ACTUAL_MISSING{JVM}, EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE, REDECLARATION{JVM}!>x2<!> = 2

actual val <!EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE, REDECLARATION{JVM}!>x3<!> = 3

// MODULE: main()()(intermediate)
actual val <!AMBIGUOUS_EXPECTS!>x1<!> = 1

actual val <!REDECLARATION!>x2<!> = 2

val <!ACTUAL_MISSING, REDECLARATION!>x3<!> = 3

/* GENERATED_FIR_TAGS: actual, expect, integerLiteral, propertyDeclaration */
