// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
//If this test hangs, it means something is broken.
package c

fun z(view: () -> Unit) {}

<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }
<!CONFLICTING_OVERLOADS!>fun x()<!> = z { z { z { z { z { z { z { z { } } } } } } } }

class x<!CONFLICTING_OVERLOADS!>()<!> {}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, functionalType, lambdaLiteral, primaryConstructor */
