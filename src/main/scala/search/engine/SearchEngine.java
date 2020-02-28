package search.engine;

import search.algorithm.MultiSearchProcessor;
import search.algorithm.SearchProcessor;

public final class SearchEngine {

  private SearchEngine() { }

  public static int indexOf(byte[] haystack, SearchProcessor processor, int from) {

    final int matchAt = indexOfImpl(haystack, processor, from);

    if (matchAt < 0) return -1;
    else return matchAt - processor.needleLength() + 1;
  }

  public static MultiSearchResult indexOfMultiple(byte[] haystack, MultiSearchProcessor processor, int from) {

    final int matchAt = indexOfImpl(haystack, processor, from);

    return matchAt < 0 ? MultiSearchResult.NOT_FOUND : new MultiSearchResult(
        matchAt - processor.needleLength() + 1,
        processor.getFoundNeedleId());
  }

  private static int indexOfImpl(byte[] haystack, SearchProcessor processor, int from) {

    int i = Math.max(from, 0);

    while (i < haystack.length) {
      if (!processor.process(haystack[i])) return i;
      i++;
    }

    return -1;
  }

}
