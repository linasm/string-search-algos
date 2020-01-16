package search.algorithm

import search.engine.SearchProcessor


object KnuthMorrisPratt {

  final class Processor(
      needle: Array[Byte],
      next: Array[Int]) extends SearchProcessor {

    private[this] var j = 0

    override val needleLength: Int = needle.length

    override def process(value: Byte): Boolean = {

      while (j > 0 && needle(j) != value) j = next(j)

      if (needle(j) == value) j += 1

      if (j == needleLength) {
        j = next(j)
        false
      } else true
    }

  }

  final class Context(
      needle: Array[Byte],
      next: Array[Int]) {

      def newProcessor: Processor = new Processor(needle, next)

  }

  def apply(needle: Array[Byte]): Context = new Context(needle, computeNext(needle))

  private def computeNext(needle: Array[Byte]): Array[Int] = {

    val next = Array.fill(needle.length + 1)(0)
    var k = 0
    var j = 1

    while (j < needle.length) {
      while (k > 0 && needle(k) != needle(j)) k = next(k)
      if (needle(k) == needle(j)) k += 1
      next(j + 1) = k
      j += 1
    }

    next
  }

}
