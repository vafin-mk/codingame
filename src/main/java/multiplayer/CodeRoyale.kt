package multiplayer

import java.util.*
import java.io.*
import java.math.*
import kotlin.collections.ArrayList

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) = CRBot(Scanner(System.`in`)).begin()

private class CRBot(val scanner: Scanner) {

  val sites = ArrayList<Site>()
  val creeps = ArrayList<Creep>()
  var sitesCount: Int = 0
  var gold: Int = 0
  var currentSiteId = -1

  private fun readWorld() {
    gold = scanner.nextInt()
    currentSiteId = scanner.nextInt() // -1 if none
    for (i in 0 until sitesCount) {
      val siteId = scanner.nextInt()
      val ignore1 = scanner.nextInt() // used in future leagues
      val ignore2 = scanner.nextInt() // used in future leagues
      val structureType = scanner.nextInt() // -1 = No structure, 2 = Barracks
      val owner = scanner.nextInt() // -1 = No structure, 0 = Friendly, 1 = Enemy
      val param1 = scanner.nextInt()
      val param2 = scanner.nextInt()

      val site = sites.find { it.id == siteId } ?: continue
      site.buildingType = BuildingType.byId(structureType)
      site.owner = Owner.byId(owner)
      when (site.buildingType) {
        BuildingType.NONE -> {}
        BuildingType.TOWER -> {}
        BuildingType.BARRACKS -> site.builtType = UnitType.byId(param2)
      }
    }
    creeps.clear()
    val numUnits = scanner.nextInt()
    for (i in 0 until numUnits) {
      val x = scanner.nextInt()
      val y = scanner.nextInt()
      val owner = scanner.nextInt()
      val unitType = scanner.nextInt() // -1 = QUEEN, 0 = KNIGHT, 1 = ARCHER
      val health = scanner.nextInt()

      creeps.add(Creep(Vector(x, y), Owner.byId(owner), UnitType.byId(unitType), health))
    }
  }

  private fun think() {
    val mySites = sites.filter { it.owner == Owner.ALLY }
    val knightsBuilt = mySites.find { it.builtType == UnitType.KNIGHT } != null
    val archersBuilt = mySites.find { it.builtType == UnitType.ARCHER } != null
    val giantBuilt = mySites.find { it.builtType == UnitType.GIANT } != null
    val queen = creeps.find { it.unitType == UnitType.QUEEN && it.owner == Owner.ALLY } ?: throw IllegalStateException("where is my queen?")
    val closestEmptySite = sites.filter { it.buildingType == BuildingType.NONE }.sortedBy { it.position.distSq(queen.position) }.firstOrNull()

    ///////////////////////////////////////
    if (closestEmptySite != null) {
      if (!knightsBuilt) {
        println(CRBuild(closestEmptySite.id, UnitType.KNIGHT))
      } else if (!archersBuilt) {
        println(CRBuild(closestEmptySite.id, UnitType.ARCHER))
      } else {
        println(CRBuildTower(closestEmptySite.id))
      }
    } else {
      val safe = Vector(1850, 50)
      if (safe.dist(queen.position) > 50.0) {
        println(CRMove(safe))
      } else {
        println(CRWait())
      }
    }
    ////////////////////////////////////
    val canTrain = gold / 80
    val buildingSites = mySites.filter { it.builtType != UnitType.QUEEN }
    val count = Math.min(canTrain, buildingSites.size)
    val siteIds = ArrayList<Int>()
    for (i in 0 until count) {
      siteIds.add(buildingSites[i].id)
    }
    println(CRTrain(siteIds))
  }

  fun begin() {
    while(true) {
      readWorld()
      think()
    }
  }

  init {
    sitesCount = scanner.nextInt()
    for (i in 0 until sitesCount) {
      val siteId = scanner.nextInt()
      val x = scanner.nextInt()
      val y = scanner.nextInt()
      val radius = scanner.nextInt()
      sites.add(Site(siteId, Vector(x, y), radius))
    }
  }
}

enum class BuildingType(val id: Int) {
  NONE(-1), TOWER(1), BARRACKS(2);
  companion object {
    fun byId(id: Int) = values().firstOrNull { it.id == id } ?: NONE
  }
}

enum class Owner(val id: Int) {
  NONE(-1), ALLY(0), RIVAL(1);
  companion object {
    fun byId(id: Int) = values().firstOrNull { it.id == id } ?: NONE
  }

}

enum class UnitType(val id: Int) {
  QUEEN(-1), KNIGHT(0), ARCHER(1), GIANT(2);
  companion object {
    fun byId(id: Int) = values().firstOrNull { it.id == id } ?: KNIGHT
  }
}

data class Site(val id: Int, val position: Vector, val radius: Int, var owner: Owner = Owner.NONE, var buildingType: BuildingType = BuildingType.NONE, var builtType: UnitType = UnitType.QUEEN)

data class Creep(val position: Vector, val owner: Owner, val unitType: UnitType, val health: Int)

class Vector(val x: Int, val y: Int) {
  fun distSq(other:Vector) = (other.x - x) * (other.x - x) + (other.y - y) * (other.y - y)
  fun dist(other:Vector) = Math.sqrt(distSq(other).toDouble())
}

class CRWait() {
  override fun toString(): String = "WAIT"
}
class CRMove(val target: Vector) {
  override fun toString(): String = "MOVE ${target.x} ${target.y}"
}
class CRBuild(val siteId: Int, val unitType: UnitType) {
  override fun toString(): String = "BUILD $siteId BARRACKS-$unitType"
}
class CRBuildTower(val siteId: Int) {
  override fun toString(): String = "BUILD $siteId TOWER"
}
class CRTrain(val siteIds: List<Int>) {
  override fun toString(): String{
    val ids = if (siteIds.isEmpty()) "" else siteIds.joinToString(separator = " ", prefix = " ") { it.toString() }
    return "TRAIN$ids"
  }
}