// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER

// KT-4034 An expression of type Nothing may not affect 'definite return' analysis

interface JavaClassifierType
interface TypeUsage
interface JetType

private fun transformClassifierType(classifierType: JavaClassifierType, howThisTypeIsUsed: TypeUsage): JetType? {
    null!!
}

/* GENERATED_FIR_TAGS: checkNotNullCall, functionDeclaration, interfaceDeclaration, nullableType */
