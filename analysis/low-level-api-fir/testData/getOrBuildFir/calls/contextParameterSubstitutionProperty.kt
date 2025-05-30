// SKIP_WHEN_OUT_OF_CONTENT_ROOT
// LANGUAGE: +ContextParameters
// MODULE: lib
// MODULE_KIND: LibraryBinary
// FILE: lib.kt
context(context: A)
public inline val <A> myContextOf: A get() = context

// MODULE: main(lib)
// FILE: main.kt

class MyClass

val withContextProp : context(MyClass)() -> Unit = {
    <expr>myContextOf == MyClass()</expr>
}
