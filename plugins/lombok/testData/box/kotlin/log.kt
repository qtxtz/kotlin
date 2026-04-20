// FULL_JDK
// FIR_DUMP

import lombok.extern.java.Log
import lombok.AccessLevel
import java.util.logging.Logger

@Log(access = AccessLevel.PUBLIC)
class LogExample {
    fun test() {
        val logLocal: Logger = log
        logLocal.info("Test LogExample")
    }
}

@Log
class LogExampleWithExistingCompanion {
    companion object Companion {}

    fun test() {
        log.warning("Test LogExampleWithExistingCompanion")
    }
}

@Log
class LogExampleWithExistingCompanionAndLogField {
    companion object MyCompanion {
        val log = "No log"
    }

    fun test(): String {
        return log
    }
}

@Log(topic = "custom topic")
class LogExampleWithTopic {
    fun test() {
        log.info("Test LogExampleWithTopic")
    }
}

class LogOnCompanion {
    @Log
    companion object {
        fun test() {
            log.info("Check @Log on companion object")
        }
    }
}

class LogOnNestedClass {
    @Log
    class Nested {
        fun test() {
            log.info("Check @Log on nested class")
        }
    }
}

class LogOnInnerClass<T> {
    @Log
    inner class Inner {
        fun test() {
            // Companion object are prohibited inside inner classes (`NESTED_CLASS_NOT_ALLOWED`), but it somehow works
            log.info("Check @Log on inner class")
        }
    }
}

@Log
object LogOnObject {
    fun test() {
        log.info("Check @Log on object")
    }
}

@Log
enum class LogOnEnum {
    ExampleEntry;

    fun test() {
        log.info("Check @Log on enum")
    }
}

fun box(): String {
    LogExample.log.info("Call from public log")
    LogExample().test()
    LogExampleWithExistingCompanion().test()
    if (LogExampleWithExistingCompanionAndLogField().test() != "No log") return "FAIL"
    LogExampleWithTopic().test()
    LogOnCompanion.test()
    LogOnNestedClass.Nested().test()
    LogOnInnerClass<String>().Inner().test()
    LogOnObject.test()
    LogOnEnum.ExampleEntry.test()
    return "OK"
}
