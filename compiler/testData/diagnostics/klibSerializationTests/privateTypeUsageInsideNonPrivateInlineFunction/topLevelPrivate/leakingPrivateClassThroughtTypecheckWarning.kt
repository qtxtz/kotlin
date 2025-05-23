// LANGUAGE: -ForbidExposureOfPrivateTypesInNonPrivateInlineFunctionsInKlibs
// DIAGNOSTICS: -NOTHING_TO_INLINE
// FIR_IDENTICAL

private class Private

internal inline fun isPrivate(obj: Any): String = when (obj) {
    <!IR_PRIVATE_TYPE_USED_IN_NON_PRIVATE_INLINE_FUNCTION_WARNING!>is <!LESS_VISIBLE_TYPE_ACCESS_IN_INLINE_WARNING!>Private<!><!> -> "isPrivate"
    else -> "OK1"
}

internal inline fun asPrivate(obj: Any): String {
    try {
        <!IR_PRIVATE_TYPE_USED_IN_NON_PRIVATE_INLINE_FUNCTION_WARNING!>val privateObj = <!IR_PRIVATE_TYPE_USED_IN_NON_PRIVATE_INLINE_FUNCTION_WARNING!>obj as <!LESS_VISIBLE_TYPE_ACCESS_IN_INLINE_WARNING!>Private<!><!><!>
        return "asPrivate"
    } catch (e: ClassCastException) {
        return "OK2"
    }
}
