// WITH_STDLIB
// TARGET_BACKEND: JVM_IR
// CHECK_BYTECODE_LISTING
// LANGUAGE: +ImplicitJvmExposeBoxed

// FILE: IC.kt
@JvmInline
value class StringWrapper(val s: String) {
    fun ok(): String = s
}

// FILE: Main.java
public class Main {
    public String test() {
        return new StringWrapper("OK").ok();
    }
}

// FILE: Box.kt
fun box(): String {
    return Main().test()
}