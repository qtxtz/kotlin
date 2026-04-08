// LL_FIR_DIVERGENCE
// This test was recently introduced in the scope of KT-85626. Unfortunately, the fix doesn't work in the Analysis API out of the box
// because it checks `isMetadataCompilation` for the session, which is only set in compiler mode.
// LL_FIR_DIVERGENCE

// METADATA_TARGET_PLATFORMS: JVM, JS
// LANGUAGE: +MultiPlatformProjects
// ISSUE: KT-85626
// WITH_STDLIB
// JVM_TARGET: 17

@kotlin.jvm.JvmRecord
data <!MISSING_DEPENDENCY_SUPERCLASS!>class Vector<!>(
    <!MISSING_DEPENDENCY_SUPERCLASS!>val x: Float<!>,
    <!MISSING_DEPENDENCY_SUPERCLASS!>val y: Float<!>,
)
