package search.algorithm

import java.lang.Byte.toUnsignedInt

import search.engine.{SearchContext, UnrolledSearchProcessor}
import sun.misc.Unsafe


object ShiftingBitMask extends SearchAlgorithm {

  import java.lang.reflect.Field

  private[this] val UNSAFE = {
    val f: Field = classOf[Unsafe].getDeclaredField("theUnsafe")
    f.setAccessible(true)
    f.get(null).asInstanceOf[Unsafe]
  }

  final class Context(
      bitMasks: Array[Long],
      successBitMask: Long,
      needleLength: Int)
    extends SearchContext with AutoCloseable {

    final class Processor(
        bitMasks: Array[Long],
        //      bitMasks1: Array[Long],
        //      bitMasks2: Array[Long],
        //      bitMasks3: Array[Long],
        //      bitMasks4: Array[Long],
        //      bitMasks5: Array[Long],
        //      bitMasks6: Array[Long],
        //      bitMasks7: Array[Long],
        unrolledBitMasksAddress: Long,
        successBitMask: Long,
        unrolledSuccessBitMask: Long,
        override val needleLength: Int) extends UnrolledSearchProcessor {

      private[this] var currentMask = 0L

      override def process(value: Byte): Boolean = {
        currentMask = ((currentMask << 1) | 1) & UNSAFE.getLong(unrolledBitMasksAddress + 8 * toUnsignedInt(value))
        //currentMask = ((currentMask << 1) | 1) & bitMasks(toUnsignedInt(value))
        (currentMask & successBitMask) == 0
      }

      override def reset(): Unit = {
        currentMask = 0L
      }

      override def processUnrolled(value: Long): Int = {

        //      var value = _value
        //
        //      currentMask = (currentMask << 8) | 255L
        //      currentMask &= bitMasks7(value.asInstanceOf[Int] & 0xFF)
        //      value >>>= 8
        //      currentMask &= bitMasks6(value.asInstanceOf[Int] & 0xFF)
        //      value >>>= 8
        //      currentMask &= bitMasks5(value.asInstanceOf[Int] & 0xFF)
        //      value >>>= 8
        //      currentMask &= bitMasks4(value.asInstanceOf[Int] & 0xFF)
        //      value >>>= 8
        //      currentMask &= bitMasks3(value.asInstanceOf[Int] & 0xFF)
        //      value >>>= 8
        //      currentMask &= bitMasks2(value.asInstanceOf[Int] & 0xFF)
        //      value >>>= 8
        //      currentMask &= bitMasks1(value.asInstanceOf[Int] & 0xFF)
        //      value >>>= 8
        //      currentMask &= bitMasks(value.asInstanceOf[Int] & 0xFF)
        //var low = value //(_value & -1).asInstanceOf[Int]
        currentMask = ((currentMask << 8) | 255L) &
          UNSAFE.getLong(unrolledBitMasksAddress + 8 * (value >>> 56)) &
          UNSAFE.getLong(unrolledBitMasksAddress + 8 * 256 + 8 * ((value >>> 48) & 0xFF)) &
          UNSAFE.getLong(unrolledBitMasksAddress + 2 * 8 * 256 + 8 * ((value >>> 40) & 0xFF)) &
          UNSAFE.getLong(unrolledBitMasksAddress + 3 * 8 * 256 + 8 * ((value >>> 32) & 0xFF)) &
          UNSAFE.getLong(unrolledBitMasksAddress + 4 * 8 * 256 + 8 * ((value >>> 24) & 0xFF)) &
          UNSAFE.getLong(unrolledBitMasksAddress + 5 * 8 * 256 + 8 * ((value >>> 16) & 0xFF)) &
          UNSAFE.getLong(unrolledBitMasksAddress + 6 * 8 * 256 + 8 * ((value >>> 8) & 0xFF)) &
          UNSAFE.getLong(unrolledBitMasksAddress + 7 * 8 * 256 + 8 * (value & 0xFF))

        //      var ml = unrolledBitMasks(low & 0xFF)
        //      low >>>= 8
        //      ml &= unrolledBitMasks(256 + (low & 0xFF))
        //      low >>>= 8
        //      ml &= unrolledBitMasks(2 * 256 + (low & 0xFF))
        //      low >>>= 8
        //      ml &= unrolledBitMasks(3 * 256 + low)

        //      var hi = (_value >>> 32).asInstanceOf[Int]
        //      var mh = unrolledBitMasks(4 * 256 + (hi & 0xFF))
        //      hi >>>= 8
        //      mh &= unrolledBitMasks(5 * 256 + (hi & 0xFF))
        //      hi >>>= 8
        //      mh &= unrolledBitMasks(6 * 256 + (hi & 0xFF))
        //      hi >>>= 8
        //      mh &= unrolledBitMasks(7 * 256 + hi)

        //currentMask = ((currentMask << 4) | 15L) & ml
        //currentMask = ((currentMask << 8) | 255L) & ml & mh

        //      currentMask &= bitMasks7(toUnsignedInt(byteBuffer.get))
        //      currentMask &= bitMasks6(toUnsignedInt(byteBuffer.get))
        //      currentMask &= bitMasks5(toUnsignedInt(byteBuffer.get))
        //      currentMask &= bitMasks4(toUnsignedInt(byteBuffer.get))
        //      currentMask &= bitMasks3(toUnsignedInt(byteBuffer.get))
        //      currentMask &= bitMasks2(toUnsignedInt(byteBuffer.get))
        //      currentMask &= bitMasks1(toUnsignedInt(byteBuffer.get))
        //      currentMask &= bitMasks(toUnsignedInt(byteBuffer.get))

        val x = currentMask & unrolledSuccessBitMask

        if (x == 0) -1
        else 64 - java.lang.Long.numberOfLeadingZeros(x)
      }

    }

    private[this] val unrolledSuccessBitMask = 255L << (needleLength - 1)
    private[this] val bitMasks1 = bitMasks.map(m => (m << 1) | 1)
    private[this] val bitMasks2 = bitMasks1.map(m => (m << 1) | 1)
    private[this] val bitMasks3 = bitMasks2.map(m => (m << 1) | 1)
    private[this] val bitMasks4 = bitMasks3.map(m => (m << 1) | 1)
    private[this] val bitMasks5 = bitMasks4.map(m => (m << 1) | 1)
    private[this] val bitMasks6 = bitMasks5.map(m => (m << 1) | 1)
    private[this] val bitMasks7 = bitMasks6.map(m => (m << 1) | 1)
    private[this] val unrolledBitMasks =
      bitMasks ++ bitMasks1 ++ bitMasks2 ++ bitMasks3 ++ bitMasks4 ++ bitMasks5 ++ bitMasks6 ++ bitMasks7

    val x = UNSAFE.allocateMemory(unrolledBitMasks.length * java.lang.Long.BYTES)
//    UNSAFE.copyMemory(unrolledBitMasks, Unsafe.ARRAY_LONG_BASE_OFFSET,
//      x, 0, unrolledBitMasks.length * java.lang.Long.BYTES)
    for (i <- unrolledBitMasks.indices) UNSAFE.putLong(x + java.lang.Long.BYTES * i, unrolledBitMasks(i))

    override def newProcessor: Processor = new Processor(
      bitMasks, x, successBitMask, unrolledSuccessBitMask, needleLength)

    override def close(): Unit = UNSAFE.freeMemory(x)
  }

  override def apply(needle: Array[Byte]): Context = new Context(
    bitMasks = computeBitMasks(needle),
    successBitMask = computeSuccessBitMask(needle),
    needleLength = needle.length)

  private def computeBitMasks(needle: Array[Byte]): Array[Long] = {
    require(needle.length <= 57, "Maximum supported search pattern length is 57.")

    val initial = -1L ^ ((1L << needle.length) - 1)

    val bitMasks = Array.fill(256)(initial)
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
