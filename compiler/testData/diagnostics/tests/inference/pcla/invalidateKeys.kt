// RUN_PIPELINE_TILL: FRONTEND
// WITH_STDLIB
fun foo(libraryInfoCache: LibraryInfoCache<String, String>, outdated: List<String>) {
    val droppedLibraryInfos = libraryInfoCache.invalidateKeys(outdated).<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>flatMapTo<!>(hashSetOf()) { <!UNRESOLVED_REFERENCE!>it<!> }
}

class LibraryInfoCache<Key, Value> {
    fun invalidateKeys(
        keys: Collection<Key>,
        validityCondition: ((Key, Value) -> Boolean)? = null
    ) {}
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, functionalType, lambdaLiteral, localProperty, nullableType,
propertyDeclaration, typeParameter */
