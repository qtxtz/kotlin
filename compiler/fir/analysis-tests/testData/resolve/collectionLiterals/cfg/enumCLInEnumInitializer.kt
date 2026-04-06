// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +CollectionLiterals
// WITH_STDLIB

inline fun <T> myRun(block: () -> T): T = block()
fun <T> myNonInlineRun(block: () -> T): T = block()

enum class E(val previous: E? = null) {
    X,
    Y(X),
    Z([]),
    T([X, Y]),
    S(<!UNINITIALIZED_ENUM_COMPANION!>run<!> { [Z] }),
    P(myRun {
        if (true) return@myRun [] else return@myRun [X]
    }),
    Q(myNonInlineRun {
        if (true) return@myNonInlineRun []
        [X]
    }),
    ; // UNINITIALIZED_ENUM_COMPANION ?

    companion object {
        operator fun of(vararg e: E): E = X
    }
}

/* GENERATED_FIR_TAGS: companionObject, enumDeclaration, enumEntry, functionDeclaration, nullableType, objectDeclaration,
operator, primaryConstructor, propertyDeclaration, vararg */
