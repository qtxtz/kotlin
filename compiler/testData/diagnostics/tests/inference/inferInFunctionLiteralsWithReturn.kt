// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
package n

//+JDK
import java.util.*

fun <T> expected(t: T, f: () -> T) : T = t

fun test(arrayList: ArrayList<Int>, list: List<Int>) {
    val t = expected(arrayList, l@ {return@l list.reverse() })
}

fun <T> List<T>.reverse() : List<T> = this
