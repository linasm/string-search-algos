package search.algorithm;

import java.util.Arrays;

/**
 * String search algorithm known as Bitap / Shift-or / Shift-and / Baeza-Yates–Gonnet.
 * https://en.wikipedia.org/wiki/Bitap_algorithm
 */
public final class ShiftingBitMask implements SearchAlgorithm {

  private final long[] bitMasks = new long[8 * 256 + 1];
  private final long successBitMask;
  private final long unrolledSuccessBitMask;
  private final int needleLength;

  public final static class Processor implements UnrolledSearchProcessor {

    private final long[] bitMasks;
    private final long successBitMask;
    private final long unrolledSuccessBitMask;
    private final int needleLength;

    private long currentMask;
    private long previouslyFound;

    private Processor(long[] bitMasks, long successBitMask, long unrolledSuccessBitMask, int needleLength) {
      this.bitMasks = bitMasks;
      this.successBitMask = successBitMask;
      this.unrolledSuccessBitMask = unrolledSuccessBitMask;
      this.needleLength = needleLength;
    }

    @Override
    public boolean process(byte value) {
      currentMask = ((currentMask << 1) | 1) & bitMasks[Byte.toUnsignedInt(value) << 3];
      return (currentMask & successBitMask) == 0;
    }

    @Override
    public boolean process(long value) {

      currentMask = ((currentMask << 8) | 255) & bitMasks[8 * 256] &
          bitMasks[(int) (((value  <<  3) & (0xFF << 3)) + 7)] &
          bitMasks[(int) (((value >>>  5) & (0xFF << 3)) + 6)] &
          bitMasks[(int) (((value >>> 13) & (0xFF << 3)) + 5)] &
          bitMasks[(int) (((value >>> 21) & (0xFF << 3)) + 4)] &
          bitMasks[(int) (((value >>> 29) & (0xFF << 3)) + 3)] &
          bitMasks[(int) (((value >>> 37) & (0xFF << 3)) + 2)] &
          bitMasks[(int) (((value >>> 45) & (0xFF << 3)) + 1)] &
          bitMasks[(int)  ((value >>> 53) & (0xFF << 3))     ];

      final long result = currentMask & unrolledSuccessBitMask;

      if (result == 0) return true;
      else {
        previouslyFound = result;
        return false;
      }
    }

    @Override
    public int needleLength() {
      return needleLength;
    }

    @Override
    public boolean hasPreviouslyFound() {
      return previouslyFound != 0;
    }

    @Override
    public int nextOffset() {
      assert previouslyFound != 0;

      final int offset = 64 - Long.numberOfLeadingZeros(previouslyFound);
      previouslyFound ^= 1L << (offset - 1);

      return offset;
    }

    @Override
    public void reset() {
      currentMask = 0;
      previouslyFound = 0;
    }

  }

  private ShiftingBitMask(byte[] needle) {

    if (needle.length > 57) {
      throw new IllegalArgumentException("Maximum supported search pattern length is 57, got " + needle.length);
    }

    final long initial = -(1L << (needle.length));
    Arrays.fill(bitMasks, initial);
    bitMasks[8 * 256] = -1L;

    long bit = 1L;
    for (byte c : needle) {
      bitMasks[Byte.toUnsignedInt(c) << 3] |= bit;
      bit <<= 1;
    }

    for (int i = 1; i < 8; i++) {
      for (int c = 0; c < 256; c++) {
        bitMasks[i + (c << 3)] = (bitMasks[i - 1 + (c << 3)] << 1) | 1;
      }
    }

    successBitMask = 1L << (needle.length - 1);
    unrolledSuccessBitMask = 255L << (needle.length - 1);
    needleLength = needle.length;
  }

  @Override
  public Processor newProcessor() {
    return new Processor(bitMasks, successBitMask, unrolledSuccessBitMask, needleLength);
  }

  public static ShiftingBitMask init(byte[] needle) {
    return new ShiftingBitMask(needle);
  }

}
