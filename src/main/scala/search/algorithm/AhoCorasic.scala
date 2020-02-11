package search.algorithm

import java.lang.Byte.toUnsignedInt

import search.engine.{MultiSearchContext, MultiSearchProcessor}

import scala.annotation.varargs
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object AhoCorasic extends MultiSearchAlgorithm {

  final class Processor(
      private[this] val jumpTable: Array[Int],
      private[this] val matchFor: Array[Int],
      private[this] val needleLengths: Array[Int]) extends MultiSearchProcessor {

    private[this] var currentPosition = 0

    override def process(value: Byte): Boolean = {
      currentPosition = jumpTable(currentPosition | toUnsignedInt(value))
      if (currentPosition >= 0) true
      else {
        currentPosition = -currentPosition
        false
      }
    }

    override def reset(): Unit = {
      currentPosition = 0
    }

    override def needleLength: Int = {
      val foundNeedleId = getFoundNeedleId
      if (foundNeedleId >= 0) needleLengths(foundNeedleId) else 0
    }

    override def getFoundNeedleId: Int = matchFor(currentPosition >>> BitsPerSymbol)

  }

  final class Context(
      jumpTable: Array[Int],
      matchFor: Array[Int],
      needleLengths: Array[Int]) extends MultiSearchContext {

      override def newProcessor: Processor = new Processor(jumpTable, matchFor, needleLengths)

  }

  private final val BitsPerSymbol = 8
  private final val AlphabetSize = 1 << BitsPerSymbol

  @varargs
  override def apply(needles: Array[Byte]*): Context = {

    require(needles.forall(_.nonEmpty), "Cannot search for an empty needle.")

    val (jumpTable, matchFor) = buildJumpTable(needles)
    linkSuffixes(jumpTable, matchFor)

    for (i <- jumpTable.indices) {
      if (matchFor(jumpTable(i) >>> BitsPerSymbol) >= 0) {
        jumpTable(i) = -jumpTable(i)
      }
    }

    new Context(jumpTable, matchFor, needles.toArray.map(_.length))
  }

  override def apply(needle: Array[Byte]): Context = apply(Seq(needle): _*)

  private def buildJumpTable(needles: Seq[Array[Byte]]): (Array[Int], Array[Int]) = {

    val emptyJumpTableSegment = Array.fill(AlphabetSize)(-1)
    val jumpTableBuilder = ArrayBuffer(emptyJumpTableSegment: _*)
    val matchForBuilder = ArrayBuffer(-1)

    for (stringId <- needles.indices) {

      val str = needles(stringId)
      var currentPosition = 0

      for (signedByte <- str) {

        val next = currentPosition + toUnsignedInt(signedByte)

        if (jumpTableBuilder(next) == -1) {
          jumpTableBuilder(next) = matchForBuilder.size << BitsPerSymbol
          jumpTableBuilder ++= emptyJumpTableSegment
          matchForBuilder += -1
        }

        currentPosition = jumpTableBuilder(next)
      }

      matchForBuilder(currentPosition >>> BitsPerSymbol) = stringId
    }

    (jumpTableBuilder.toArray, matchForBuilder.toArray)
  }

  private def linkSuffixes(jumpTable: Array[Int], matchFor: Array[Int]): Unit = {

    val queue = mutable.Queue(0)
    val suffixLinks = Array.fill(matchFor.length)(-1)

    while (queue.nonEmpty) {

      val v = queue.dequeue()
      val u = if (v == 0) 0 else suffixLinks(v >>> BitsPerSymbol)

      if (matchFor(v >>> BitsPerSymbol) == -1) matchFor(v >>> BitsPerSymbol) = matchFor(u >>> BitsPerSymbol)

      for (ch <- 0 until AlphabetSize) {

        val vIndex = v | ch
        val uIndex = u | ch

        if (jumpTable(vIndex) != -1) {
          suffixLinks(jumpTable(vIndex) >>> BitsPerSymbol) = if (v > 0 && jumpTable(uIndex) != -1) jumpTable(uIndex) else 0
          queue += jumpTable(vIndex)
        } else {
          jumpTable(vIndex) = if (jumpTable(uIndex) != -1) jumpTable(uIndex) else 0
        }
      }
    }
  }

}
