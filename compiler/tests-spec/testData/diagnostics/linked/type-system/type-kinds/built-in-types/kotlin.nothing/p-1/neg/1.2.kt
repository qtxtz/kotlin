// !DIAGNOSTICS: -UNUSED_VARIABLE -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_VALUE -UNUSED_PARAMETER
// !LANGUAGE: +NewInference
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-100
 * PLACE: type-system, type-kinds, built-in-types, kotlin.nothing -> paragraph 1 -> sentence 2
 * NUMBER: 1
 * DESCRIPTION: The use of Boolean literals as the identifier (with backtick) in the class.
 * HELPERS: checkType, functions
 */


// TESTCASE NUMBER: 1
class Case1(val nothing: Nothing)

fun case1() {
    val res: Case1(Nothing())
}