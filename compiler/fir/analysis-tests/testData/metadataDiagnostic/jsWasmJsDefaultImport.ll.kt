// LL_FIR_DIVERGENCE
// Metadata default import providers in the Analysis API are not yet correctly set up. See KT-82220.
// LL_FIR_DIVERGENCE

// METADATA_TARGET_PLATFORMS: JS, WasmJs
// OPT_IN: kotlin.js.ExperimentalJsExport

// `JsExport` should be available as a default import from `import kotlin.js.*`, as it's imported by both JS and WasmJ.
@<!UNRESOLVED_REFERENCE!>JsExport<!>
fun test(): String = "test"
