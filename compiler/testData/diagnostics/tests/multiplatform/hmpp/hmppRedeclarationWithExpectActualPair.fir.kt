// IGNORE_FIR_DIAGNOSTICS
// RUN_PIPELINE_TILL: BACKEND
// LATEST_LV_DIFFERENCE

// MODULE: common

expect class A

expect class B

<!DUPLICATE_CLASS_NAMES!>class <!CLASSIFIER_REDECLARATION!>C<!><!>

// MODULE: intermediate()()(common)

<!DUPLICATE_CLASS_NAMES!>actual class <!CLASSIFIER_REDECLARATION!>A<!><!>

<!DUPLICATE_CLASS_NAMES!>class <!ACTUAL_MISSING, ACTUAL_MISSING{METADATA}, CLASSIFIER_REDECLARATION!>B<!><!>

expect class C

// MODULE: main()()(common, intermediate)

<!DUPLICATE_CLASS_NAMES!>class <!ACTUAL_MISSING!>A<!><!>

<!DUPLICATE_CLASS_NAMES!>actual class B<!>

<!DUPLICATE_CLASS_NAMES!>actual class C<!>

/* GENERATED_FIR_TAGS: actual, classDeclaration, expect */
