package search.algorithm;

/**
 * Knuth-Morris-Pratt string search algorithm.
 * https://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm
 */
public final class KnuthMorrisPratt implements SearchAlgorithm {

  private final int[] jumpTable;
  private final byte[] needle;

  private static class Processor implements SearchProcessor {

    private final byte[] needle;
    private final int[] jumpTable;

    private int currentPosition;

    private Processor(byte[] needle, int[] jumpTable) {
      this.needle = needle;
      this.jumpTable = jumpTable;
    }

    @Override
    public boolean process(byte value) {
      while (currentPosition > 0 && needle[currentPosition] != value) {
        currentPosition = jumpTable[currentPosition];
      }
      if (needle[currentPosition] == value) {
        currentPosition++;
      }
      if (currentPosition == needle.length) {
        currentPosition = jumpTable[currentPosition];
        return false;
      }

      return true;
    }

    @Override
    public int needleLength() {
      return needle.length;
    }

    @Override
    public void reset() {
      currentPosition = 0;
    }

  }

  private KnuthMorrisPratt(byte[] needle) {
    this.needle = needle.clone();
    this.jumpTable = new int[needle.length + 1];

    int j = 0;
    for (int i = 1; i < needle.length; i++) {
      while (j > 0 && needle[j] != needle[i]) {
        j = jumpTable[j];
      }
      if (needle[j] == needle[i]) {
        j++;
      }
      jumpTable[i + 1] = j;
    }
  }

  @Override
  public Processor newProcessor() {
    return new Processor(needle, jumpTable);
  }

  public static KnuthMorrisPratt init(byte[] needle) {
    return new KnuthMorrisPratt(needle);
  }

}
