// FULL_JDK
// WITH_STDLIB

import lombok.extern.java.Log
import lombok.AccessLevel

@Log(access = AccessLevel.PUBLIC)
class LogExamplePublic

@Log(access = AccessLevel.PROTECTED)
open class LogExampleProtectedBase

class LogExampleProtected : LogExampleProtectedBase() {
    fun test() {
        log.info("Test LogExampleProtected") // OK
    }
}

@Log(access = AccessLevel.PRIVATE)
open class LogExamplePrivateBase {
    fun testBase() {
        log.info("Test LogExamplePrivateBase") // OK
    }
}

class LogExamplePrivate : LogExamplePrivateBase() {
    fun test() {
        <!INVISIBLE_REFERENCE!>log<!>.info("Test LogExamplePrivate") // Invisible
    }
}

<!WRONG_ANNOTATION_TARGET!>@Log<!> // Prohibited
fun func() {}

<!WRONG_ANNOTATION_TARGET!>@Log<!> // Prohibited
typealias TA = String

val logOnAnonymousObject = @Log object {} // Prohibited, because it's not possible to create companion inside anonymous object

fun check() {
    @Log
    class LocalClass

    LogExamplePublic.log.info("Test LogExamplePublic") // OK
    LogExampleProtected.<!UNRESOLVED_REFERENCE!>log<!>.info("Test LogExampleProtected") // INVISIBLE
    LogExamplePrivate.<!UNRESOLVED_REFERENCE!>log<!>.info("Test LogExamplePrivate") // INVISIBLE
}
