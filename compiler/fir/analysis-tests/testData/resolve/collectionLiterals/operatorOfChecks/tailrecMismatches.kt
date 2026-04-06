// RUN_PIPELINE_TILL: BACKEND
// ISSUE: KT-80492
// LANGUAGE: +CollectionLiterals

class MyList<T> {
    companion object {
        operator fun of(): MyList<Int> = MyList()
        <!NO_TAIL_CALLS_FOUND!>tailrec<!> operator fun of(vararg lams: Int): MyList<Int> {
            if (lams.size == 1) return []
            val x: MyList<Int> = [1, 2, 3]
            takeLst([1, 2, 3])
            return [lams[0]]
        }
    }
}

fun takeLst(lst: MyList<Int>) { }

/* GENERATED_FIR_TAGS: classDeclaration, companionObject, functionDeclaration, nullableType, objectDeclaration, operator,
suspend, typeParameter, vararg */
