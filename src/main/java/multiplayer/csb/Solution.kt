import java.util.*
import java.io.*
import java.math.*
import kotlin.collections.ArrayList

val checkPointRadius = 300
val minBoostDist = 1800
val brakeDist1 = 1300
val brakeDist2 = 1100
val brakeDist3 = 800
var boostUsed = false
var firstLapPassed = false

fun main(args: Array<String>) {
  val input = Scanner(System.`in`)
  var path = ArrayList<Vector>()

  while (true) {
    val x = input.nextInt()
    val y = input.nextInt()
    val nextCheckpointX = input.nextInt()
    val nextCheckpointY = input.nextInt()
    val nextCheckpointDist = input.nextInt()
    val nextCheckpointAngle = input.nextInt()
    val opponentX = input.nextInt()
    val opponentY = input.nextInt()

    val checkPoint = Vector(nextCheckpointX, nextCheckpointY)
    val checkPointIndex = path.indexOf(checkPoint)
    if (!firstLapPassed && checkPointIndex == 0 && path.size > 1) {
      firstLapPassed = true
    }

    var target = Vector(nextCheckpointX, nextCheckpointY)

    if (checkPointIndex == -1) {
      path.add(checkPoint)
      target = findTarget(Vector(x, y), checkPoint)
    } else if (firstLapPassed) {
      val nextCheckPoint = if (checkPointIndex == path.size - 1) path[0] else path[checkPointIndex + 1]
      target = findTarget(nextCheckPoint, checkPoint)
    }

    val speed = findSpeed(nextCheckpointDist, nextCheckpointAngle)

    println("$target $speed")
  }
}

fun findTarget(current: Vector, checkPoint: Vector): Vector {
  val m = (checkPoint.y - current.y) / (checkPoint.x - current.x)
  val b = checkPoint.y - m * checkPoint.x

  val x1 = (checkPoint.x + checkPointRadius / Math.sqrt((1 + m * m).toDouble())).toInt()
  val x2 = (checkPoint.x - checkPointRadius / Math.sqrt((1 + m * m).toDouble())).toInt()

  val vector1 = Vector(x1, m * x1 + b)
  val vector2 = Vector(x2, m * x2 + b)

  return if (vector1.sqrtDist(current) < vector2.sqrtDist(current)) vector1 else vector2
}

fun findSpeed(dist: Int, angle: Int): Any {
  System.err.println("$dist --- $angle -- $boostUsed -- $firstLapPassed")
  return when {
    Math.abs(angle) > 90 -> 0
    !boostUsed && firstLapPassed && angle == 0 && dist >= minBoostDist -> {
      boostUsed = true
      "BOOST"
    }
    dist < brakeDist3 -> 25
    dist < brakeDist2 -> 50
    dist < brakeDist1 -> 75
    else -> 100
  }
}

class Vector(val x: Int, val y: Int) {

  fun subtract(other: Vector): Vector {
    return Vector(x - other.x, y - other.x)
  }

  fun mult(multiplier: Int): Vector {
    return Vector(x * multiplier, y * multiplier)
  }

  fun sqrtDist(other: Vector): Int {
    val xOffset = x - other.x
    val yOffset = y - other.y

    return xOffset * xOffset + yOffset * yOffset
  }

  override fun toString(): String {
    return "$x $y"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Vector

    if (x != other.x) return false
    if (y != other.y) return false

    return true
  }

  override fun hashCode(): Int {
    var result = x
    result = 31 * result + y
    return result
  }
}