// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// FILE: KotlinFile.kt
fun foo(javaClass: JavaClass) {
    javaClass.КакоеТоСвойство = javaClass.КакоеТоСвойство + 1
    javaClass.<!UNRESOLVED_REFERENCE!>какоеТоСвойство<!>

    javaClass.КУ
    javaClass.<!UNRESOLVED_REFERENCE!>ку<!>
}

// FILE: JavaClass.java
public class JavaClass {
    public int getКакоеТоСвойство() {
        return 0;
    }

    public void setКакоеТоСвойство(int value) {
    }

    public int getКУ() {
        return 0;
    }
}

/* GENERATED_FIR_TAGS: additiveExpression, assignment, functionDeclaration, integerLiteral, javaProperty, javaType */
