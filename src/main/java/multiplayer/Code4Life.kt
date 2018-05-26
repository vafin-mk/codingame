import java.util.*
import kotlin.collections.ArrayList

/**
 * Bring data on patient samples from the diagnosis machine to the laboratory with enough molecules to produce medicine!
 **/
fun main(args: Array<String>) = C4LBot(Scanner(System.`in`)).start()

private class C4LBot(val scanner: Scanner) {

  private val PLAYERS_COUNT = 2
  private val MAX_CARRY_SAMPLES = 3
  private val MAX_CARRY_MOLECULES = 10

  private val projects = ArrayList<Storage>()
  private val samples = ArrayList<Sample>()
  private var gameStorage = zeroStorage()
  private var ally: Player = Player(Module.LABORATORY, 0, 0, zeroStorage(), zeroStorage())
  private var rival: Player = Player(Module.LABORATORY, 0, 0, zeroStorage(), zeroStorage())

  private val commandsQueue = LinkedList<C4LCommand>()

  fun start() {
    while (true) {
      readInput()
      if (commandsQueue.isNotEmpty()) {
        val nextCommand = commandsQueue.poll()
        if (isStillValidCommand(nextCommand)) {
          println(nextCommand.execute())
          continue
        } else {
          debug("invalidate commands")
          commandsQueue.clear()
        }
      }
      val commands = think()
      debug("commands: $commands")
      commandsQueue.addAll(commands)
      println(commandsQueue.poll().execute())
    }
  }

  private fun isStillValidCommand(command: C4LCommand): Boolean {
    if (command is ConnectSample) {
      val sample = samples.firstOrNull { it.id == command.sample.id }
      if (sample == null || sample.carrier == Carrier.RIVAL) return false
    }
    return true
  }

  private fun readInput() {
    for (i in 0 until PLAYERS_COUNT) {
      val target = scanner.next()
      val eta = scanner.nextInt()
      val score = scanner.nextInt()
      val storageA = scanner.nextInt()
      val storageB = scanner.nextInt()
      val storageC = scanner.nextInt()
      val storageD = scanner.nextInt()
      val storageE = scanner.nextInt()
      val expertiseA = scanner.nextInt()
      val expertiseB = scanner.nextInt()
      val expertiseC = scanner.nextInt()
      val expertiseD = scanner.nextInt()
      val expertiseE = scanner.nextInt()

      val storage = Storage(storageA, storageB, storageC, storageD, storageE)
      val expertise = Storage(expertiseA, expertiseB, expertiseC, expertiseD, expertiseE)

      if (i == 0) {
        ally = Player(Module.valueOf(target), eta, score, storage, expertise)
      } else {
        rival = Player(Module.valueOf(target), eta, score, storage, expertise)
      }
    }

    val availableA = scanner.nextInt()
    val availableB = scanner.nextInt()
    val availableC = scanner.nextInt()
    val availableD = scanner.nextInt()
    val availableE = scanner.nextInt()
    gameStorage = Storage(availableA, availableB, availableC, availableD, availableE)

    samples.clear()
    val sampleCount = scanner.nextInt()
    for (i in 0 until sampleCount) {
      val sampleId = scanner.nextInt()
      val carriedBy = scanner.nextInt()
      val rank = scanner.nextInt()
      val expertiseGain = scanner.next()
      val health = scanner.nextInt()
      val costA = scanner.nextInt()
      val costB = scanner.nextInt()
      val costC = scanner.nextInt()
      val costD = scanner.nextInt()
      val costE = scanner.nextInt()
      val sampleCost = Storage(costA, costB, costC, costD, costE)
      samples.add(Sample(sampleId, Carrier.fromId(carriedBy), Rank.fromRank(rank), Molecule.fromStr(expertiseGain), health, sampleCost - ally.expertise))
    }
  }

  private fun think(): List<C4LCommand> {
    val carriedSamples = carriedSamples()
    if (carriedSamples.isEmpty()) {
      return pickupSamples()
    }

    val undiagnosedSamples = carriedSamples.filter { !it.diagnosed() }
    if (undiagnosedSamples.isNotEmpty()) {
      return diagnoseSamples(undiagnosedSamples)
    }

    if (!canFinishSamples(carriedSamples)) {
      return pickupMolecules(carriedSamples)
    }

    return finishSamples(carriedSamples)
  }

  private fun pickupSamples(): List<C4LCommand> {
    val commands = ArrayList<C4LCommand>()
    if (ally.position != Module.SAMPLES) {
      commands.add(C4LMove(Module.SAMPLES))
    }
//    commands.add(FetchSample(Rank.LOW))
//    commands.add(FetchSample(Rank.LOW))
    commands.add(FetchSample(Rank.MEDIUM))
//    val combinations = combineSamples()
//    if (combinations.isNotEmpty()) {
//      val bestCombination = combinations.filter { it.combinedStorage.sum() <= MAX_CARRY_MOLECULES }.sortedByDescending { it.score }.firstOrNull()
//      debug("best combo -- $bestCombination")
//      bestCombination?.samples?.forEach { commands.add(ConnectSample(it)) }
//    }
    return commands
  }

  private fun diagnoseSamples(undiagnosedSamples: List<Sample>): List<C4LCommand> {
    val commands = ArrayList<C4LCommand>()
    if (ally.position != Module.DIAGNOSIS) {
      commands.add(C4LMove(Module.DIAGNOSIS))
    }
    undiagnosedSamples.mapTo(commands) { ConnectSample(it) }
    return commands
  }

  private fun pickupMolecules(carriedSamples: List<Sample>): List<C4LCommand> {
    val commands = ArrayList<C4LCommand>()
    if (ally.position != Module.MOLECULES) {
      commands.add(C4LMove(Module.MOLECULES))
    }
    val combinedCost = carriedSamples.combinedCost()
    val required = combinedCost - ally.storage
    for (i in 0 until required.aCount) commands.add(ConnectMolecule(Molecule.A))
    for (i in 0 until required.bCount) commands.add(ConnectMolecule(Molecule.B))
    for (i in 0 until required.cCount) commands.add(ConnectMolecule(Molecule.C))
    for (i in 0 until required.dCount) commands.add(ConnectMolecule(Molecule.D))
    for (i in 0 until required.eCount) commands.add(ConnectMolecule(Molecule.E))
    return commands
  }

  private fun finishSamples(carriedSamples: List<Sample>): List<C4LCommand> {
    val commands = ArrayList<C4LCommand>()
    if (ally.position != Module.LABORATORY) {
      commands.add(C4LMove(Module.LABORATORY))
    }

    carriedSamples.mapTo(commands) { ConnectSample(it) }
    return commands
  }

  private fun canFinishSamples(carriedSamples: List<Sample>): Boolean {
    val combinedCost = carriedSamples.combinedCost()
    return (combinedCost - ally.storage).empty()
  }

  //todo some refactor required
  private fun combineSamples(): List<SampleCombination> {
    val samples = cloudSamples()
    if (samples.size > 10) {
      debug("Too much samples; bottleneck here!!!")
    }
    val combinations = ArrayList<SampleCombination>()
    for (i in 0 until samples.size) {
      val firstSample = samples[i]
      combinations.add(combine(firstSample))
      for (j in i + 1 until samples.size) {
        val secondSample = samples[j]
        combinations.add(combine(firstSample, secondSample))
        for (k in j + 1 until samples.size) {
          val thirdSample = samples[k]
          combinations.add(combine(firstSample, secondSample, thirdSample))
        }
      }
    }

    return combinations
  }

  private fun combine(vararg samples: Sample): SampleCombination {
    val combinedSamples = ArrayList<Sample>()
    var score = 0
    var cost = zeroStorage()

    for (sample in samples) {
      combinedSamples.add(sample)
      score += sample.score
      cost += sample.cost
    }

    return SampleCombination(combinedSamples, score, cost)
  }

  private fun carriedSamples(): List<Sample> = samples.filter { it.carrier == Carrier.ALLY }
  private fun cloudSamples() = samples.filter { it.carrier == Carrier.CLOUD }

  private fun debug(message: String) = System.err.println(message)
  private fun zeroStorage() = Storage(0, 0, 0, 0, 0)

  val projectCount: Int

  init {
    projectCount = scanner.nextInt()
    for (i in 0 until projectCount) {
      val a = scanner.nextInt()
      val b = scanner.nextInt()
      val c = scanner.nextInt()
      val d = scanner.nextInt()
      val e = scanner.nextInt()
      projects.add(Storage(a, b, c, d, e))
    }
  }
}

enum class Molecule {
  A, B, C, D, E, UNKNOWN;
  companion object {
    fun fromStr(str: String): Molecule = values().firstOrNull { str == it.name } ?: UNKNOWN
  }
}

private enum class Module {
  DIAGNOSIS, MOLECULES, LABORATORY, SAMPLES, START_POS
}

enum class Rank(val rank: Int) {
  LOW(1), MEDIUM(2), HIGH(3);

  companion object {
    fun fromRank(rank: Int): Rank {
      return when(rank) {
        1 -> LOW
        2 -> MEDIUM
        3 -> HIGH
        else -> throw IllegalStateException("oops: $rank")
      }
    }
  }
}

enum class Carrier {
  ALLY, RIVAL, CLOUD;

  companion object {
    fun fromId(id: Int): Carrier {
      return when (id) {
        0 -> ALLY
        1 -> RIVAL
        -1 -> CLOUD
        else -> throw IllegalStateException("the fuck?: $id")
      }
    }
  }
}

//private data class Project(val aCost: Int, val bCost: Int, val cCost: Int, val dCost: Int, val eCost: Int)
data class Sample(val id: Int, val carrier: Carrier, val rank: Rank, val reward: Molecule, val score: Int, val cost: Storage)

private data class Player(val position: Module, val eta: Int, val score: Int, val storage: Storage, val expertise: Storage)
data class Storage(val aCount: Int, val bCount: Int, val cCount: Int, val dCount: Int, val eCount: Int)
//private data class Expertise(val aExp: Int, val bExp: Int, val cExp: Int, val dExp: Int, val eExp: Int)
private data class SampleCombination(val samples: List<Sample>, val score: Int, val combinedStorage: Storage)

operator fun Storage.plus(other: Storage): Storage = Storage(
    aCount + other.aCount,
    bCount + other.bCount,
    cCount + other.cCount,
    dCount + other.dCount,
    eCount + other.eCount
)

operator fun Storage.minus(other: Storage): Storage = Storage(
    aCount - other.aCount,
    bCount - other.bCount,
    cCount - other.cCount,
    dCount - other.dCount,
    eCount - other.eCount
)

fun Storage.sum(): Int = aCount + bCount + cCount + dCount + eCount
fun Storage.empty(): Boolean = aCount <= 0 && bCount <= 0 && cCount <= 0 && dCount <= 0 && eCount <= 0
fun Iterable<Sample>.combinedCost(): Storage {
  var cost = Storage(0, 0, 0, 0, 0)
  for (sample in this) {
    cost += sample.cost
  }
  return cost
}

fun Sample.diagnosed() = score > 0

private sealed class C4LCommand {
  abstract fun execute(): String
}

private data class C4LMove(val module: Module) : C4LCommand() {
  override fun execute(): String = "GOTO $module"
}

private data class ConnectSample(val sample: Sample) : C4LCommand() {
  override fun execute(): String = "CONNECT ${sample.id}"
}

private data class FetchSample(val rank: Rank) : C4LCommand() {
  override fun execute(): String = "CONNECT ${rank.rank}"
}

private data class ConnectMolecule(val molecule: Molecule) : C4LCommand() {
  override fun execute(): String = "CONNECT $molecule"
}

private class C4LWait: C4LCommand() {
  override fun execute(): String = "WAIT"
}