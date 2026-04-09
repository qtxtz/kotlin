// LL_FIR_DIVERGENCE
// Composable session components in the Analysis API are not yet correctly set up. See KT-82220.
// LL_FIR_DIVERGENCE

// METADATA_TARGET_PLATFORMS: JVM, JS
// DIAGNOSTICS: -NOTHING_TO_INLINE

// `FirJvmInlineCheckerComponent` prohibits local inline functions.
fun test() {
    inline fun localInline() = 42
    localInline()
}
