// FULL_JDK
// FIR_DUMP

// FILE: test.kt

import lombok.extern.java.Log

@Log
class LogExample {
    fun test() {
        myLog.info("Test nonstatic logger with custom name")
    }
}

open class Base {
    val myLog = "no log"
}

@Log
class Derived : Base() {
    fun test() {
        myLog.info("test Derived") // `Derived.log` is more prioritized than `Base.log`
    }
}

fun box(): String {
    LogExample().test()
    Derived().test()
    return "OK"
}

// FILE: lombok.config
lombok.log.fieldName=myLog
lombok.log.fieldIsStatic=false
