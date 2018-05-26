import java.util.*
import java.io.*
import java.math.*
import kotlin.collections.ArrayList

fun main(args : Array<String>) {
  val input = Scanner(System.`in`)

  var ash: Point
  val humans = ArrayList<Human>()
  val zombies = ArrayList<Zombie>()
  while (true) {
    val x = input.nextInt()
    val y = input.nextInt()
    ash = Point(x, y)

    humans.clear()
    val humanCount = input.nextInt()
    for (i in 0 until humanCount) {
      val humanId = input.nextInt()
      val humanX = input.nextInt()
      val humanY = input.nextInt()
      humans.add(Human(humanId, humanX, humanY))
    }

    zombies.clear()
    val zombieCount = input.nextInt()
    for (i in 0 until zombieCount) {
      val zombieId = input.nextInt()
      val zombieX = input.nextInt()
      val zombieY = input.nextInt()
      val zombieXNext = input.nextInt()
      val zombieYNext = input.nextInt()
      zombies.add(Zombie(zombieId, zombieX, zombieY, zombieXNext, zombieYNext))
    }

    val closestZombie = zombies.sortedBy { z -> z.distSquared(ash) }[0]

    println(closestZombie)
  }
}

open class Point(val x: Int, val y: Int) {
  fun distSquared(other: Point) : Int = ((other.x - x) * (other.x - x)) + ((other.y - y) * (other.y - y))
  override fun toString(): String = "$x $y"
}

class Human(val id: Int, x: Int, y: Int) : Point(x, y)
class Zombie(val id: Int, x: Int, y: Int, val nextX: Int, val nextY: Int) : Point(x, y)

val WIDTH = 16000
val HEIGHT = 9000
val SPEED = 1000
val RANGE = 2000
val Z_SPEED = 400