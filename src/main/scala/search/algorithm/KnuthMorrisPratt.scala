package search.algorithm

import search.engine.{SearchContext, SearchProcessor}


object KnuthMorrisPratt extends SearchAlgorithm {

  final class Processor(
      needle: Array[Byte],
      jumpTable: Array[Int])
    extends SearchProcessor {

    private[this] var j = 0

    override val needleLength: Int = needle.length

    override def process(value: Byte): Boolean = {

      while (j > 0 && needle(j) != value) {
        j = jumpTable(j)
      }
      if (needle(j) == value) {
        j += 1
      }

      if (j == needleLength) {
        j = jumpTable(j)
        false
      } else true
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
