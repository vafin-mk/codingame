import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Bot(val scanner: Scanner) {
  private val factoryCount: Int = scanner.nextInt()
  private val factories = HashMap<Int, Factory>()
  private val shortPath = HashMap<Edge, Int>()//key = from -> to factories, value - intermediate factory to fastest route
  private val pathDistance = HashMap<Edge, Int>()//key = from -> to factories, value - distance
  private val routeConnectors = HashMap<Int, Int>() //id --> how many routes it connects
  private val factoryValue = HashMap<Int, Double>() //id --> value
  private val troops = ArrayList<Troop>()
  private var turn = 0

  init {
    for (id in 0 until factoryCount) {
      factories.put(id, Factory(id, 0, 0, 0, 0))
      routeConnectors.put(id, 0)
      factoryValue.put(id, 0.0)
    }

    val linkCount = scanner.nextInt()
    val edges = HashMap<Edge, Int>()
    for (i in 0 until linkCount) {
      val factory1 = scanner.nextInt()
      val factory2 = scanner.nextInt()
      val distance = scanner.nextInt()
      edges.put(Edge(factory1, factory2), distance)
      edges.put(Edge(factory2, factory1), distance)
    }

    for (id in 0 until factoryCount) {
      findShortestPath(id, edges)
    }

    shortPath
        .filter { it.key.destination != it.value }
        .map { it.value }
        .forEach { routeConnectors.put(it, (routeConnectors[it] ?: 0) + 1) }
  }

  //http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
  private fun findShortestPath(source: Int, edges: Map<Edge, Int>) {
    val settled = HashSet<Int>()
    val unsettled = HashSet<Int>()
    val predecessors = HashMap<Int, Int>() //id - id
    val distance = HashMap<Int, Int>() //id - dist

    distance.put(source, 0)
    unsettled.add(source)

    while(unsettled.isNotEmpty()) {
      val closestId = distance.filter { unsettled.contains(it.key) }.minBy { it.value }?.key ?: continue
      settled.add(closestId)
      unsettled.remove(closestId)
      val closestDist = distance[closestId] ?: throw IllegalStateException()
      edges.filter { it.key.source == closestId && !settled.contains(it.key.destination)}
          .map { it.key.destination }
          .forEach { destination->
            val destinationDist = distance[destination] ?: Int.MAX_VALUE
            val edgeDist = edges[Edge(closestId, destination)] ?: throw IllegalStateException()
            if (destinationDist > closestDist + edgeDist) {
              distance.put(destination, closestDist + edgeDist)
              predecessors.put(destination, closestId)
              unsettled.add(destination)
            }
      }
    }

    for (destination in 0 until factoryCount) {
      if (destination == source) continue
      val edge = Edge(source, destination)
      val shortDist = distance[destination] ?: throw IllegalStateException()
      val edgeDist = edges[edge] ?: throw IllegalStateException()

      val path = LinkedList<Int>()
      var step = destination
      path.add(step)
      while (true) {
        step = predecessors[step] ?: break
        path.add(step)
      }
      path.reverse()

      if (shortDist + (path.size - 2) <= edgeDist) {
        shortPath.put(edge, path[1])
        pathDistance.put(edge, shortDist + (path.size - 2))
      } else {
        shortPath.put(edge, edge.destination)
        pathDistance.put(edge, edgeDist)
      }
    }
  }

  fun begin() {
    while (true) {
      readWorld()
      sendActions()
      turn++
    }
  }

  private fun readWorld() {
    val entityCount = scanner.nextInt()
    troops.clear()
    for (i in 0 until entityCount) {
      val entityId = scanner.nextInt()
      val entityType = scanner.next()
      val arg1 = scanner.nextInt()
      val arg2 = scanner.nextInt()
      val arg3 = scanner.nextInt()
      val arg4 = scanner.nextInt()
      val arg5 = scanner.nextInt()

      when(entityType) {
        "FACTORY" -> {
          val factory = factories[entityId] ?: throw IllegalStateException()
          with(factory) {
            owner = arg1
            cyborgCount = arg2
            production = arg3
            disabledTurns = arg4
          }
        }
        "TROOP" -> {
          troops.add(Troop(entityId, arg1, arg2, arg3, arg4, arg5))
        }
        "BOMB" -> {

        }
      }
    }

    factories.values.forEach { factory ->
      factoryValue.put(factory.id, factory.production + 0.01 * Math.pow(routeConnectors[factory.id]?.toDouble() ?: 0.0, 0.5) + 0.1)
    }
  }

  private fun sendActions() {
    val actions = think()
    val string = actions.joinToString(separator = ";") { action ->
      when(action) {
        is Move -> "MOVE ${action.source} ${action.destination} ${action.cyborgCount}"
        is Bomb -> "BOMB ${action.source} ${action.destination}"
        is Inc -> "INC ${action.factory}"
        Wait -> "WAIT"
        is Message -> "MSG ${action.message}"
      }
    }
    println(string)
  }

  private fun think() : List<Action> {
    val result = ArrayList<Action>()
    result.add(Wait)
    result.add(Message("Greetings Traveler!"))

    val allyFactories = factories.values.filter { it.owner == OWNER_ALLY }
    for (factory in allyFactories) {
      var unitsToUse = factory.cyborgCount - requiredForDefence(factory)
      if (unitsToUse <= 0) continue

      val actions = ArrayList<ActionScore>()
      for (targetId in 0 until factoryCount) {
        val targetFactory = factories[targetId] ?: continue
        when{
          targetId == factory.id -> actions.add(ActionScore(Inc(targetId), increaseScore(targetFactory)))
          targetFactory.owner == OWNER_ALLY ->  {
            actions.add(ActionScore(Move(factory.id, targetFactory.id, defenceReinforcementsRequired(targetFactory)), defenceScore(factory, targetFactory)))
          }
          targetFactory.owner == OWNER_NEUTRAL -> {
            actions.add(ActionScore(Move(factory.id, targetFactory.id, requiredToCaptureNeutral(targetFactory)), neutralAttackScore(factory, targetFactory)))
          }
          targetFactory.owner == OWNER_RIVAL -> {
            actions.add(ActionScore(Move(factory.id, targetFactory.id, 0), rivalAttackScore(factory, targetFactory)))
          }
        }
      }

      actions.sortByDescending { it.score }
//      printerr("$actions")
      for (action in actions.map { it.action }) {
        when(action) {
          is Inc -> {
            unitsToUse -= 10
            result.add(action)
          }
          is Move -> {
            val targetFactory = factories[action.destination] ?: throw IllegalStateException()
            when (targetFactory.owner) {
              OWNER_ALLY -> {
                var use = defenceReinforcementsRequired(targetFactory)
                if (use > unitsToUse) {
                  use = unitsToUse
                }
                result.add(Move(action.source, shortPath[Edge(action.source, action.destination)] ?: -1, use))
                unitsToUse -= use
              }
              OWNER_NEUTRAL -> {
                var use = requiredToCaptureNeutral(targetFactory)
                if (use > unitsToUse) {
                  use = unitsToUse
                }
                result.add(Move(action.source, shortPath[Edge(action.source, action.destination)] ?: -1, use))
                unitsToUse -= use
              }
              OWNER_RIVAL -> {
                result.add(Move(action.source, shortPath[Edge(action.source, action.destination)] ?: -1, unitsToUse))
                unitsToUse = 0
              }
            }
          }
        }
        if (unitsToUse <= 0) break
      }

      if (turn == 0) {
        //send bombs
        var bombsAvailable = 2
        val rivalFactory = factories.values.first { it.owner == OWNER_RIVAL }
        if (rivalFactory.production >= 2) {
          bombsAvailable--
          result.add(Bomb(factory.id, rivalFactory.id))
        }

        val neutrals = factories.values
            .filter {
              it.owner == OWNER_NEUTRAL
                && ((pathDistance[Edge(rivalFactory.id, it.id)] ?: 22) < (pathDistance[Edge(factory.id, it.id)] ?: 22))
            }
            .sortedBy { factoryValue[it.id] }.reversed()

        for ((id) in neutrals) {
          bombsAvailable--
          result.add(Bomb(factory.id, id))
          if (bombsAvailable == 0) break
        }
      }
    }

    return result
  }

  private fun increaseScore(factory: Factory) : Double {
    return if (factory.production == 3) Double.MIN_VALUE else 1.0 / Math.pow(10.0, 1.6)
  }

  private fun defenceScore(source: Factory, destination: Factory) : Double {
    val value = factoryValue[destination.id] ?: 0.0
    val dist = pathDistance[Edge(source.id, destination.id)]?.toDouble() ?: 22.0

    return value / (Math.pow(dist, 2.0) * defenceReinforcementsRequired(destination))
  }

  private fun neutralAttackScore(source: Factory, destination: Factory) : Double {
    val value = factoryValue[destination.id] ?: 0.0
    val dist = pathDistance[Edge(source.id, destination.id)]?.toDouble() ?: 22.0

    return value / (Math.pow(dist, 2.0) * requiredToCaptureNeutral(destination))
  }

  private fun rivalAttackScore(source: Factory, destination: Factory) : Double {
    val value = factoryValue[destination.id] ?: 0.0
    val dist = pathDistance[Edge(source.id, destination.id)]?.toDouble() ?: 22.0

    return value / (Math.pow(dist, 2.0) * 8)
  }

  private fun requiredForDefence(factory: Factory, lookAheadTurns: Int = 5) : Int {
    val incomingTroops = troops.filter { it.destination == factory.id && it.turnsToArrive <= lookAheadTurns }
    val myUnits = incomingTroops.filter { it.owner == OWNER_ALLY }.map { it.cyborgCount }.sum() + factory.production * lookAheadTurns
    val enemyUnits = incomingTroops.filter { it.owner == OWNER_RIVAL }.map { it.cyborgCount }.sum()

    return Math.max(0, enemyUnits - myUnits)
  }

  private fun requiredToCaptureNeutral(factory: Factory) : Int {
    val incomingTroops = troops.filter { it.destination == factory.id }
    val myUnits = incomingTroops.filter { it.owner == OWNER_ALLY }.map { it.cyborgCount }.sum()
    val enemyUnits = incomingTroops.filter { it.owner == OWNER_RIVAL }.map { it.cyborgCount }.sum()

    return Math.max(1, factory.cyborgCount + enemyUnits - myUnits)
  }

  private fun defenceReinforcementsRequired(factory: Factory, lookAheadTurns: Int = 5) : Int {
    val incomingTroops = troops.filter { it.destination == factory.id && it.turnsToArrive <= lookAheadTurns }
    val myUnits = incomingTroops.filter { it.owner == OWNER_ALLY }.map { it.cyborgCount }.sum() + factory.production * lookAheadTurns + factory.cyborgCount
    val enemyUnits = incomingTroops.filter { it.owner == OWNER_RIVAL }.map { it.cyborgCount }.sum()

    return if (myUnits > enemyUnits) 0 else (-1 * (myUnits - enemyUnits))
  }
}

data class Factory(val id: Int, var owner: Int, var cyborgCount: Int, var production: Int, var disabledTurns: Int)
data class Troop(val id: Int, var owner: Int, var source: Int, var destination: Int, var cyborgCount: Int, var turnsToArrive: Int)
data class Explosive(val id: Int, var owner: Int, var source: Int, var destination: Int, var turnsToArrive: Int)

data class Edge(val source: Int, val destination: Int)

sealed class Action
data class Move(val source: Int, val destination: Int, val cyborgCount: Int) : Action()
data class Bomb(val source: Int, val destination: Int) : Action()
data class Inc(val factory: Int) : Action()
object Wait : Action()
data class Message(val message: String) : Action()

data class ActionScore(val action: Action, val score: Double)

//constants
val OWNER_ALLY = 1
val OWNER_NEUTRAL = 0
val OWNER_RIVAL = -1

val UNKNOWN = -1


fun printerr(message: String) = System.err.println(message)
fun main(args : Array<String>) = Bot(Scanner(System.`in`)).begin()