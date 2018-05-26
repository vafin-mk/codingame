package puzzles.easy.mimetype

import java.util.*
import kotlin.collections.HashMap

fun main(args : Array<String>) {
  val input = Scanner(System.`in`)
  val mimeTypesCount = input.nextInt()
  val filesCount = input.nextInt()
  val map = HashMap<String, String>(mimeTypesCount)
  for (i in 0 until mimeTypesCount) {
    val extension = input.next() // file extension
    val mimeType = input.next() // MIME type.
    map[extension.toLowerCase()] = mimeType
  }
  input.nextLine()

  for (i in 0 until filesCount) {
    val filename = input.nextLine() // One file name per line.
    if (!filename.contains(".")) {
      println("UNKNOWN")
      continue
    }
    val extension = filename.substring(filename.indexOf(".") + 1).toLowerCase()
    println(map.getOrDefault(extension, "UNKNOWN"))
  }
}