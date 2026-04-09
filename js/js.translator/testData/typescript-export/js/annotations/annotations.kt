// CHECK_TYPESCRIPT_DECLARATIONS
// SKIP_NODE_JS
// INFER_MAIN_MODULE
// LANGUAGE: +JsAllowExportingAnnotationClasses

// MODULE: JS_TESTS
// FILE: annotations.kt

package foo

@JsExport
annotation class Simple

@Retention(AnnotationRetention.SOURCE)
@JsExport
annotation class WithStringParam(val message: String)

@Retention(AnnotationRetention.BINARY)
@JsExport
annotation class WithMultipleParams(val name: String, val count: Int)

@Retention(AnnotationRetention.RUNTIME)
@JsExport
annotation class WithDefaultValue(val level: Int = 0)

@Target(AnnotationTarget.CLASS)
@JsExport
annotation class WithBooleanParam(val enabled: Boolean)

@JsExport
fun box() = "OK"
