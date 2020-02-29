package search.algorithm;

import java.util.Arrays;

public final class ShiftingBitMask implements SearchAlgorithm {

  private final long[] bitMasks = new long[8 * 256];
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
      currentMask = ((currentMask << 1) | 1) & bitMasks[Byte.toUnsignedInt(value)];
      return (currentMask & successBitMask) == 0;
    }

    @Override
    public boolean process(long value) {

      currentMask = ((currentMask << 8) | 255) &
          bitMasks[(int) (7 * 256 + ((value       ) & 0xFF))] &
          bitMasks[(int) (6 * 256 + ((value >>>  8) & 0xFF))] &
          bitMasks[(int) (5 * 256 + ((value >>> 16) & 0xFF))] &
          bitMasks[(int) (4 * 256 + ((value >>> 24) & 0xFF))] &
          bitMasks[(int) (3 * 256 + ((value >>> 32) & 0xFF))] &
          bitMasks[(int) (2 * 256 + ((value >>> 40) & 0xFF))] &
          bitMasks[(int) (    256 + ((value >>> 48) & 0xFF))] &
          bitMasks[(int)             (value >>> 56)         ];

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
    Arrays.fill(bitMasks, 0, 256, initial);

    long bit = 1L;
    for (byte c : needle) {
      bitMasks[Byte.toUnsignedInt(c)] |= bit;
      bit <<= 1;
    }

    for (int i = 1; i < 8; i++) {
      for (int c = 0; c < 256; c++) {
        bitMasks[i * 256 + c] = (bitMasks[(i - 1) * 256 + c] << 1) | 1;
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
