// WITH_STDLIB
// TARGET_BACKEND: JVM_IR
// CHECK_BYTECODE_LISTING

// FILE: Test.kt
@file:OptIn(ExperimentalStdlibApi::class)

@JvmExposeBoxed
class ExposedResult {
    fun consume(result: Result<String>): String = result.getOrThrow()

    fun <T: Result<String>> consume1(result: T): String = result.getOrThrow()
}

// FILE: Main.java
public class Main {
    public String test(kotlin.Result<String> result) {
        return new ExposedResult().consume(result);
    }
    public String test1(kotlin.Result<String> result) {
        return new ExposedResult().consume1(result);
    }
}

// FILE: Box.kt
fun box(): String  {
    var result = Main().test(Result.success("OK"))
    if (result != "OK") return result
    return Main().test1(Result.success("OK"))
}
