package puzzles.easy.horse

import java.util.*
import kotlin.collections.ArrayList

fun main(args : Array<String>) {
  val input = Scanner(System.`in`)
  val N = input.nextInt()
  val horses = ArrayList<Int>()
  for (i in 0 until N) horses.add(input.nextInt())
  var diff = 1000000
  horses.sorted().reduce { a, b ->
    if (b - a < diff) diff = b - a
    b
  }
  println(diff)
}