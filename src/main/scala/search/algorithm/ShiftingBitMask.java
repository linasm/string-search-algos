package search.algorithm;

public final class ShiftingBitMask implements SearchAlgorithm {

  private final long[] bitMasks = new long[256];
  private final long successBitMask;
  private final int needleLength;

  public final static class Processor implements SearchProcessor {

    private final long[] bitMasks;
    private final long successBit;
    private final int needleLength;

    private long currentMask;

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
    public int needleLength() {
      return needleLength;
    }

    @Override
    public void reset() {
      currentMask = 0;
    }

  }

  private ShiftingBitMask(byte[] needle) {

    if (needle.length > 64) {
      throw new IllegalArgumentException("Maximum supported search pattern length is 64, got " + needle.length);
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
