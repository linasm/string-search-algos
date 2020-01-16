package search.algorithm

import java.lang.Byte.toUnsignedInt

import search.engine.SearchProcessor


object ShiftingBitMask {

  final class Processor(
      private val bitMasks: Array[Long],
      private val successBitMask: Long,
      override val needleLength: Int) extends SearchProcessor {

    private var currentMask = 0L

    override def process(value: Byte): Boolean = {
      currentMask = ((currentMask << 1) | 1) & bitMasks(toUnsignedInt(value))
      (currentMask & successBitMask) == 0
    }

  }

  final class Context(
      val bitMasks: Array[Long],
      val successBitMask: Long,
      val needleLength: Int) {

      def newProcessor: Processor = new Processor(bitMasks, successBitMask, needleLength)

  }

  def apply(needle: Array[Byte]): Context = new Context(
    bitMasks = computeBitMasks(needle),
    successBitMask = computeSuccessBitMask(needle),
    needleLength = needle.length)

  private def computeBitMasks(needle: Array[Byte]): Array[Long] = {
    require(needle.length <= 64, "Maximum supported search pattern length is 64.")

    val bitMasks = Array.ofDim[Long](256)
    var bit = 1L
    for (c <- needle) {
      bitMasks(toUnsignedInt(c)) |= bit
      bit <<= 1
    }

    bitMasks
  }

  private def computeSuccessBitMask(needle: Array[Byte]): Long = {
    1L << (needle.length - 1)
  }

}
