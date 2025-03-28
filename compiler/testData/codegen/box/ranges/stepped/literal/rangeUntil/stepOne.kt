// Auto-generated by GenerateSteppedRangesCodegenTestData. Do not edit!
// WITH_STDLIB
// LANGUAGE: +RangeUntilOperator
@file:OptIn(ExperimentalStdlibApi::class)
import kotlin.test.*

fun box(): String {
    val intList = mutableListOf<Int>()
    for (i in 1..<5 step 1) {
        intList += i
    }
    assertEquals(listOf(1, 2, 3, 4), intList)

    val longList = mutableListOf<Long>()
    for (i in 1L..<5L step 1L) {
        longList += i
    }
    assertEquals(listOf(1L, 2L, 3L, 4L), longList)

    val charList = mutableListOf<Char>()
    for (i in 'a'..<'e' step 1) {
        charList += i
    }
    assertEquals(listOf('a', 'b', 'c', 'd'), charList)

    return "OK"
}