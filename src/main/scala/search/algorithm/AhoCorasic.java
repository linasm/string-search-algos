package search.algorithm;

import com.google.common.base.Preconditions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

public final class AhoCorasic implements MultiSearchAlgorithm {

  private static final int BITS_PER_SYMBOL = 8;
  private static final int ALPHABET_SIZE = 1 << BITS_PER_SYMBOL;

  private final int[] jumpTable;
  private final int[] matchForNeedleId;
  private final int[] needleLengths;

  private final static class Processor implements MultiSearchProcessor {

    private final int[] jumpTable;
    private final int[] matchForNeedleId;
    private final int[] needleLengths;

    private int currentPosition;

    private Processor(int[] jumpTable, int[] matchForNeedleId, int[] needleLengths) {
      this.jumpTable = jumpTable;
      this.matchForNeedleId = matchForNeedleId;
      this.needleLengths = needleLengths;
    }

    @Override
    public boolean process(byte value) {
      currentPosition = jumpTable[currentPosition | Byte.toUnsignedInt(value)];
      if (currentPosition < 0) {
        currentPosition = -currentPosition;
        return false;
      }
      return true;
    }

    @Override
    public int getFoundNeedleId() {
      return matchForNeedleId[currentPosition >> BITS_PER_SYMBOL];
    }

    @Override
    public void reset() {
      currentPosition = 0;
    }

    @Override
    public int needleLength() {
      int foundNeedleId = getFoundNeedleId();
      return foundNeedleId >= 0 ? needleLengths[foundNeedleId] : 0;
    }

  }

  private AhoCorasic(byte[]... needles) {

    for (byte[] needle : needles) {
      Preconditions.checkArgument(needle.length > 0, "Needle must be non empty");
    }

    ArrayList<Integer> jumpTableBuilder = new ArrayList<>(ALPHABET_SIZE);
    for (int i = 0; i < ALPHABET_SIZE; i++) {
      jumpTableBuilder.add(-1);
    }

    ArrayList<Integer> matchForBuilder = new ArrayList<>();
    matchForBuilder.add(-1);

    needleLengths = new int[needles.length];

    for (int needleId = 0; needleId < needles.length; needleId++) {
      byte[] needle = needles[needleId];
      needleLengths[needleId] = needle.length;
      int currentPosition = 0;

      for (byte ch0 : needle) {

        final int ch = Byte.toUnsignedInt(ch0);
        final int next = currentPosition + ch;

        if (jumpTableBuilder.get(next) == -1) {
          jumpTableBuilder.set(next, jumpTableBuilder.size());
          for (int i = 0; i < ALPHABET_SIZE; i++) {
            jumpTableBuilder.add(-1);
          }
          matchForBuilder.add(-1);
        }

        currentPosition = jumpTableBuilder.get(next);
      }

      matchForBuilder.set(currentPosition >> BITS_PER_SYMBOL, needleId);
    }

    jumpTable = new int[jumpTableBuilder.size()];
    matchForNeedleId = new int[matchForBuilder.size()];

    for (int i = 0; i < jumpTableBuilder.size(); i++) {
      jumpTable[i] = jumpTableBuilder.get(i);
    }

    for (int i = 0; i < matchForBuilder.size(); i++) {
      matchForNeedleId[i] = matchForBuilder.get(i);
    }

    linkSuffixes();

    for (int i = 0; i < jumpTable.length; i++) {
      if (matchForNeedleId[jumpTable[i] >> BITS_PER_SYMBOL] >= 0) {
        jumpTable[i] = -jumpTable[i];
      }
    }
  }

  private void linkSuffixes() {

    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(0);

    int[] suffixLinks = new int[matchForNeedleId.length];
    Arrays.fill(suffixLinks, -1);

    while (!queue.isEmpty()) {

      final int v = queue.remove();
      int vPosition = v >> BITS_PER_SYMBOL;
      final int u = suffixLinks[vPosition] == -1 ? 0 : suffixLinks[vPosition];

      if (matchForNeedleId[vPosition] == -1) {
        matchForNeedleId[vPosition] = matchForNeedleId[u >> BITS_PER_SYMBOL];
      }

      for (int ch = 0; ch < ALPHABET_SIZE; ch++) {

        final int vIndex = v | ch;
        final int uIndex = u | ch;

        final int jumpV = jumpTable[vIndex];
        final int jumpU = jumpTable[uIndex];

        if (jumpV != -1) {
          suffixLinks[jumpV >> BITS_PER_SYMBOL] = v > 0 && jumpU != -1 ? jumpU : 0;
          queue.add(jumpV);
        } else {
          jumpTable[vIndex] = jumpU != -1 ? jumpU : 0;
        }
      }
    }
  }

  @Override
  public MultiSearchProcessor newProcessor() {
    return new Processor(jumpTable, matchForNeedleId, needleLengths);
  }

  public static AhoCorasic init(byte[] ...needles) {
    return new AhoCorasic(needles);
  }

}
