package puzzles.easy.onboarding

import java.util.*
fun main(args : Array<String>) {
  val input = Scanner(System.`in`)
  while (true) {
    val enemy1 = input.next()
    val dist1 = input.nextInt()
    val enemy2 = input.next()
    val dist2 = input.nextInt()
    println(if (dist1 < dist2) enemy1 else enemy2)
  }
}