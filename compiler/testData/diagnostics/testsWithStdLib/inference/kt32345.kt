// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: -UNUSED_VARIABLE

import kotlin.reflect.KProperty
import kotlin.properties.ReadWriteProperty

class CleanupTestExample {
    val cleanUpBlocks: MutableList<Pair<Any, (Any) -> Unit>> = mutableListOf()

    class CleaningDelegate<T : Any?>(
        initialValue: T? = null,
        val cleanupBlocks: MutableList<Pair<Any, (Any) -> Unit>>,
        val block: (T) -> Unit
    ) : ReadWriteProperty<Any?, T> {
        private var value: T? = initialValue

        init {
            addCleanupBlock(initialValue)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
        }

        @Suppress("UNCHECKED_CAST")
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            addCleanupBlock(value)
            this.value = value
        }

        fun addCleanupBlock(value: T?) {
            if (value != null) {
                @Suppress("UNCHECKED_CAST")
                cleanupBlocks.add((<!DEBUG_INFO_SMARTCAST!>value<!> to block) as Pair<Any, (Any) -> Unit>)
            }

        }
    }

    data class TestHolder(val num: Int)

    fun <T : Any?> cleanup(initialValue: T? = null, block: (T) -> Unit) = CleaningDelegate(initialValue, cleanUpBlocks, block)

    fun testWithCleanup() {
        val testHolder = TestHolder(1)

        var thing: TestHolder by CleaningDelegate(testHolder, cleanupBlocks = cleanUpBlocks, block = { println("cleaning up $it") })
        var thing2: TestHolder by cleanup(testHolder) { println("cleaning up $it") }
    }
}

/* GENERATED_FIR_TAGS: asExpression, assignment, classDeclaration, data, dnnType, elvisExpression, equalityExpression,
functionDeclaration, functionalType, ifExpression, init, integerLiteral, lambdaLiteral, localProperty, nestedClass,
nullableType, operator, override, primaryConstructor, propertyDeclaration, propertyDelegate, setter, smartcast,
starProjection, stringLiteral, thisExpression, typeConstraint, typeParameter */
