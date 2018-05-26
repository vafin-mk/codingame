package puzzles.easy.defibrillators

import java.util.*

fun main(args: Array<String>) {
  val input = Scanner(System.`in`)
  val userLongitude = input.next().replace(",", ".").toDouble()
  val userLatitude = input.next().replace(",", ".").toDouble()
  val N = input.nextInt()
  if (input.hasNextLine()) input.nextLine()

  var closestName = ""
  var closestDist = Double.MAX_VALUE

  for (i in 0 until N) {
    val info = input.nextLine().replace(",", ".").split(";")
    val longitude = info[info.size - 2].toDouble()
    val latitude = info[info.size - 1].toDouble()

    val x = (longitude - userLongitude) * Math.cos((userLatitude + latitude) / 2)
    val y = latitude - userLatitude
    val dist = Math.sqrt(x * x + y * y) * 6371
    if (dist < closestDist) {
      closestDist = dist
      closestName = info[1]
    }
  }
  println(closestName)
}