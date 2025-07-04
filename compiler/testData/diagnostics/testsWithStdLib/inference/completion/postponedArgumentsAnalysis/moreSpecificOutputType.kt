// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
interface ObservableSet<out T> : Set<T> {}

fun <K> test(x: List<ObservableSet<K>>) {
    x.reduce { acc: Set<K>, set: Set<K> -> acc + set }
}

/* GENERATED_FIR_TAGS: additiveExpression, functionDeclaration, interfaceDeclaration, lambdaLiteral, nullableType, out,
typeParameter */
