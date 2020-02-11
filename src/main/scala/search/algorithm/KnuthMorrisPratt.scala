package search.algorithm

import search.engine.{SearchContext, SearchProcessor}


object KnuthMorrisPratt extends SearchAlgorithm {

  final class Processor(
      needle: Array[Byte],
      jumpTable: Array[Int])
    extends SearchProcessor {

    private[this] var currentPosition = 0

    override val needleLength: Int = needle.length

    override def process(value: Byte): Boolean = {

      while (currentPosition > 0 && needle(currentPosition) != value) {
        currentPosition = jumpTable(currentPosition)
      }
      if (needle(currentPosition) == value) {
        currentPosition += 1
      }

      if (currentPosition == needleLength) {
        currentPosition = jumpTable(currentPosition)
        false
      } else true
    }

    override def reset(): Unit = {
      currentPosition = 0
    }

  }

  final class Context(
      needle: Array[Byte],
      jumpTable: Array[Int])
    extends SearchContext {

      override def newProcessor: Processor = new Processor(needle, jumpTable)

  }

  override def apply(needle: Array[Byte]): Context = new Context(needle, computeJumpTable(needle))

  private def computeJumpTable(needle: Array[Byte]): Array[Int] = {

    val jumpTable = Array.fill(needle.length + 1)(0)
    var k = 0
    var j = 1

    while (j < needle.length) {
      while (k > 0 && needle(k) != needle(j)) k = jumpTable(k)
      if (needle(k) == needle(j)) k += 1
      jumpTable(j + 1) = k
      j += 1
    }

    jumpTable
  }

}
