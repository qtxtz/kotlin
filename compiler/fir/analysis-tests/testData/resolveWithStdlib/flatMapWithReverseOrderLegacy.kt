// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: -FixationEnhancementsIn22
// FULL_JDK
// STDLIB_JDK8
// JVM_TARGET: 1.8
// ISSUE: KT-71854

// FILE: Wrapper.java

import java.util.Comparator;
import java.util.Collections;

public class Wrapper {
    public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
        return Collections.reverseOrder();
    }
}

// FILE: flatMapWithReverseOrder.kt
import java.util.TreeSet

class MergeFragment {
    val tailsAndBody = mutableSetOf<Int>()
}

fun specifyTypeTest(fragments: Set<MergeFragment>) {
    val flatMap = fragments.flatMapTo(TreeSet(Wrapper.reverseOrder())) { f -> f.tailsAndBody }

    for (f in flatMap) {
        testFun(<!ARGUMENT_TYPE_MISMATCH!>f<!>)
    }

}

fun testFun(i: Int) {}

/* GENERATED_FIR_TAGS: capturedType, classDeclaration, flexibleType, forLoop, functionDeclaration, inProjection,
javaFunction, lambdaLiteral, localProperty, nullableType, propertyDeclaration */
