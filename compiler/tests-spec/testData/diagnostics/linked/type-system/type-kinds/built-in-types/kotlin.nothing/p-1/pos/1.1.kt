
// !DIAGNOSTICS: -UNUSED_VARIABLE -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_VALUE -UNUSED_PARAMETER
// !LANGUAGE: +NewInference
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-100
 * PLACE: type-system, type-kinds, built-in-types, kotlin.nothing -> paragraph 1 -> sentence 1
 * RELEVANT PLACES: type-system, introduction-1 -> paragraph 7 -> sentence 2
 * NUMBER: 1
 * DESCRIPTION: The use of Boolean literals as the identifier (with backtick) in the class.
 * HELPERS: checkType
 */


// TESTCASE NUMBER: 1
class Case1() {
    val data: Nothing
}

class CustomClass

fun case1(c: Case1) {
    checkSubtype<Any>(c.data)
    checkSubtype<Function<Nothing>>(c.data)
    checkSubtype<Int>(c.data)
    checkSubtype<Short>(c.data)
    checkSubtype<Byte>(c.data)
    checkSubtype<Long>(c.data)
    checkSubtype<kotlin.Array>(c.data)
    checkSubtype<CustomClass>(c.data)
}