// RUN_PIPELINE_TILL: FRONTEND
// WITH_STDLIB
// LANGUAGE: +EagerLambdaAnalysis
// ISSUE: KT-85014
// ISSUE: KT-85031

// FILE: JavaClass.java
import org.jetbrains.annotations.*;
import java.util.List;

public class JavaClass {
    public static int foo6(List<Integer> x) { return 0; }
    public static long foo6(List<Long> x) { return 0; }

    public static int foo7(List<Integer> x) { return 0; }
    public static long foo7(List<@NotNull Long> x) { return 0; }

    public static int foo8(List<@NotNull Integer> x) { return 0; }
    public static long foo8(List<Long> x) { return 0; }

    public static int foo9(List<@Nullable Integer> x) { return 0; }
    public static long foo9(List<Long> x) { return 0; }

    public static int foo10(List<Integer> x) { return 0; }
    public static long foo10(List<@Nullable Long> x) { return 0; }
}

// FILE: main.kt

fun foo1(x: Int?): Int = TODO()
fun foo1(x: Long): Long = TODO()

fun foo2(x: List<Int?>): Int = TODO()
fun foo2(x: List<Long>): Long = TODO()

fun foo3(x: List<Int?>): Int = TODO()
fun foo3(x: List<Long?>): Long = TODO()

fun foo4(x: List<Int>): Int = TODO()
fun foo4(x: List<Long?>): Long = TODO()

fun foo5(x: List<Int>): Int = TODO()
fun foo5(x: List<Long>): Long = TODO()

fun main() {
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>foo1(1)<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>foo1(1L)<!>

    <!DEBUG_INFO_EXPRESSION_TYPE("ERROR CLASS: Ambiguity: foo2, [/foo2, /foo2]")!><!OVERLOAD_RESOLUTION_AMBIGUITY!>foo2<!>(listOf(1))<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>foo2(listOf(1L))<!>
    
    <!DEBUG_INFO_EXPRESSION_TYPE("ERROR CLASS: Ambiguity: foo4, [/foo4, /foo4]")!><!OVERLOAD_RESOLUTION_AMBIGUITY!>foo4<!>(listOf(1))<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>foo4(listOf(1L))<!>
    
    <!DEBUG_INFO_EXPRESSION_TYPE("ERROR CLASS: Ambiguity: foo5, [/foo5, /foo5]")!><!OVERLOAD_RESOLUTION_AMBIGUITY!>foo5<!>(listOf(1))<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>foo5(listOf(1L))<!>

    <!DEBUG_INFO_EXPRESSION_TYPE("ERROR CLASS: Ambiguity: foo6, [/JavaClass.foo6, /JavaClass.foo6]")!>JavaClass.<!OVERLOAD_RESOLUTION_AMBIGUITY!>foo6<!>(listOf(1))<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>JavaClass.foo6(listOf(1L))<!>

    <!DEBUG_INFO_EXPRESSION_TYPE("ERROR CLASS: Ambiguity: foo7, [/JavaClass.foo7, /JavaClass.foo7]")!>JavaClass.<!OVERLOAD_RESOLUTION_AMBIGUITY!>foo7<!>(listOf(1))<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>JavaClass.foo7(listOf(1L))<!>

    <!DEBUG_INFO_EXPRESSION_TYPE("ERROR CLASS: Ambiguity: foo8, [/JavaClass.foo8, /JavaClass.foo8]")!>JavaClass.<!OVERLOAD_RESOLUTION_AMBIGUITY!>foo8<!>(listOf(1))<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>JavaClass.foo8(listOf(1L))<!>

    <!DEBUG_INFO_EXPRESSION_TYPE("ERROR CLASS: Ambiguity: foo9, [/JavaClass.foo9, /JavaClass.foo9]")!>JavaClass.<!OVERLOAD_RESOLUTION_AMBIGUITY!>foo9<!>(listOf(1))<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>JavaClass.foo9(listOf(1L))<!>

    <!DEBUG_INFO_EXPRESSION_TYPE("ERROR CLASS: Ambiguity: foo10, [/JavaClass.foo10, /JavaClass.foo10]")!>JavaClass.<!OVERLOAD_RESOLUTION_AMBIGUITY!>foo10<!>(listOf(1))<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Long")!>JavaClass.foo10(listOf(1L))<!>
}

/* GENERATED_FIR_TAGS: functionDeclaration, functionalType, integerLiteral, lambdaLiteral */
