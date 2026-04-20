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

class LogOnNestedClass {
    @Log
    class Nested {
        fun test() {
            myLog.info("Check @Log on nested class")
        }
    }
}

class LogOnInnerClass<T> {
    @Log
    inner class Inner {
        fun test() {
            myLog.info("Check @Log on inner class")
        }
    }
}

@Log
object LogOnObject {
    fun test() {
        myLog.info("Check @Log on object")
    }
}

@Log
enum class LogOnEnum {
    ExampleEntry;

    fun test() {
        myLog.info("Check @Log on enum")
    }
}

fun box(): String {
    LogExample().test()
    Derived().test()
    LogOnNestedClass.Nested().test()
    LogOnInnerClass<String>().Inner().test()
    LogOnObject.test()
    LogOnEnum.ExampleEntry.test()
    return "OK"
}

// FILE: lombok.config
lombok.log.fieldName=myLog
lombok.log.fieldIsStatic=false
