package search.algorithm

import java.lang.Byte.toUnsignedInt

import search.engine.{SearchContext, SearchProcessor}


object ShiftingBitMask {

  final class Processor(
      bitMasks: Array[Long],
      successBitMask: Long,
      override val needleLength: Int) extends SearchProcessor {

    private[this] var currentMask = 0L

    override def process(value: Byte): Boolean = {
      currentMask = ((currentMask << 1) | 1) & bitMasks(toUnsignedInt(value))
      (currentMask & successBitMask) == 0
    }

  }

  final class Context(
      bitMasks: Array[Long],
      successBitMask: Long,
      needleLength: Int)
    extends SearchContext {

      override def newProcessor: Processor = new Processor(bitMasks, successBitMask, needleLength)

  }

  def apply(needle: Array[Byte]): Context = new Context(
    bitMasks = computeBitMasks(needle),
    successBitMask = computeSuccessBitMask(needle),
    needleLength = needle.length)

  private def computeBitMasks(needle: Array[Byte]): Array[Long] = {
    require(needle.length <= 64, "Maximum supported search pattern length is 64.")

    val bitMasks = Array.ofDim[Long](256)
    var bit = 1L
    var i = 0

    while (i < needle.length) {
      val c = toUnsignedInt(needle(i))
      bitMasks(c) |= bit
      bit <<= 1
      i += 1
    }

    bitMasks
  }

  private def computeSuccessBitMask(needle: Array[Byte]): Long = {
    1L << (needle.length - 1)
  }

}
