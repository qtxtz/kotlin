// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-56520 (case 7, object vs static member, no companions in scope)
// FIR_DUMP

// FILE: some/Some.kt
package some

class Some {
    object foo {} // (1)

    // companion object {}
}

// FILE: some3/Some.java
package some3;

public class Some {
    public static int foo = 3; // (3)
}

// FILE: main.kt
import some.*
import some3.*

fun test() = <!NONE_APPLICABLE!>Some<!>.<!UNRESOLVED_REFERENCE!>foo<!>

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, nestedClass, objectDeclaration */
