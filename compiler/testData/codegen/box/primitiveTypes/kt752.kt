// TARGET_BACKEND: JVM_IR
package demo_range

operator fun Int?.rangeTo(other : Int?) : IntRange = this!!.rangeTo(other!!)

fun box() : String {
    val x : Int? = 10
    val y : Int? = 12

    for (i in x..y)
      System.out?.println(i.inv())
    return "OK"
}
