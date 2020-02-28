package search.algorithm;

public final class ShiftingBitMask implements SearchAlgorithm {

  private final long[] bitMasks = new long[256];
  private final long successBitMask;
  private final int needleLength;

  private final static class Processor implements UnrolledSearchProcessor {

    private final long[] bitMasks;
    private final long successBit;
    private final int needleLength;

    private long currentMask;
    private long previouslyFound;

    private Processor(long[] bitMasks, long successBit, int needleLength) {
      this.bitMasks = bitMasks;
      this.successBit = successBit;
      this.needleLength = needleLength;
    }

    @Override
    public boolean process(byte value) {
      currentMask = ((currentMask << 1) | 1) & bitMasks[Byte.toUnsignedInt(value)];
      return (currentMask & successBit) == 0;
    }

    @Override
    public boolean process(long value) {

      long result;

      currentMask = ((currentMask << 1) | 1) & bitMasks[(int) (value & 0xFF)];
      result = currentMask & successBit;

      currentMask = ((currentMask << 1) | 1) & bitMasks[(int) (value >>> 8) & 0xFF];
      result = (result << 1) | (currentMask & successBit);

      currentMask = ((currentMask << 1) | 1) & bitMasks[(int) (value >>> 16) & 0xFF];
      result = (result << 1) | (currentMask & successBit);

      currentMask = ((currentMask << 1) | 1) & bitMasks[(int) (value >>> 24) & 0xFF];
      result = (result << 1) | (currentMask & successBit);

      currentMask = ((currentMask << 1) | 1) & bitMasks[(int) (value >>> 32) & 0xFF];
      result = (result << 1) | (currentMask & successBit);

      currentMask = ((currentMask << 1) | 1) & bitMasks[(int) (value >>> 40) & 0xFF];
      result = (result << 1) | (currentMask & successBit);

      currentMask = ((currentMask << 1) | 1) & bitMasks[(int) (value >>> 48) & 0xFF];
      result = (result << 1) | (currentMask & successBit);

      currentMask = ((currentMask << 1) | 1) & bitMasks[(int) (value >>> 56)];
      result = (result << 1) | (currentMask & successBit);

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
    public void reset() {
      currentMask = 0;
      previouslyFound = 0;
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

  }

  private ShiftingBitMask(byte[] needle) {

    if (needle.length > 57) {
      throw new IllegalArgumentException("Maximum supported search pattern length is 57, got " + needle.length);
    }

    long bit = 1L;
    for (byte c : needle) {
      bitMasks[Byte.toUnsignedInt(c)] |= bit;
      bit <<= 1;
    }

    successBitMask = 1L << (needle.length - 1);
    needleLength = needle.length;
  }

  @Override
  public Processor newProcessor() {
    return new Processor(bitMasks, successBitMask, needleLength);
  }

  public static ShiftingBitMask init(byte[] needle) {
    return new ShiftingBitMask(needle);
  }

}
