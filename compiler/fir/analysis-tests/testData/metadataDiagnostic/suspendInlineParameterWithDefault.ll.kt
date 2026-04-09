// LL_FIR_DIVERGENCE
// Composable session components in the Analysis API are not yet correctly set up. See KT-82220.
// LL_FIR_DIVERGENCE

// METADATA_TARGET_PLATFORMS: JVM, JS
// DIAGNOSTICS: -NOTHING_TO_INLINE

// `FirJvmInlineCheckerComponent` prohibits inlining suspend lambdas with default values.
inline suspend fun inlineWithSuspendDefault(
    crossinline block: suspend () -> Unit = {}
) {
    block()
}
