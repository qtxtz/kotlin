// Auto-generated by GenerateSteppedRangesCodegenTestData. Do not edit!
// WITH_STDLIB
// LANGUAGE: +RangeUntilOperator
@file:OptIn(ExperimentalStdlibApi::class)
import kotlin.test.*

fun box(): String {
    val intList = mutableListOf<Int>()
    for (i in 1..<7 step 2 step 1) {
        intList += i
    }
    assertEquals(listOf(1, 2, 3, 4, 5), intList)

    val longList = mutableListOf<Long>()
    for (i in 1L..<7L step 2L step 1L) {
        longList += i
    }
    assertEquals(listOf(1L, 2L, 3L, 4L, 5L), longList)

    val charList = mutableListOf<Char>()
    for (i in 'a'..<'g' step 2 step 1) {
        charList += i
    }
    assertEquals(listOf('a', 'b', 'c', 'd', 'e'), charList)

    return "OK"
}