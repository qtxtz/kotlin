// WITH_STDLIB
@file:Suppress("RESERVED_MEMBER_INSIDE_VALUE_CLASS")

import kotlin.test.*

private inline class Z(val data: Int) {
    override fun equals(other: Any?) = other is Z && data % 256 == other.data % 256
}

fun box(): String {
    assertTrue(Z(0) == Z(256))
    return "OK"
}
