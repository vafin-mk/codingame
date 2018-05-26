package puzzles.easy.thedescent

import java.util.*

fun main(args : Array<String>) {
  val input = Scanner(System.`in`)
  while (true) {
    var index = 0
    var max = 0
    for (i in 0 until 8) {
      val height = input.nextInt()
      if (height > max) {
        max = height
        index = i
      }
    }
    println(index)
  }
}