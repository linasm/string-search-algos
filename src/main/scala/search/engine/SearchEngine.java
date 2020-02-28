package search.engine;

import search.algorithm.MultiSearchProcessor;
import search.algorithm.SearchProcessor;

import java.nio.ByteBuffer;

import static search.engine.MultiSearchResult.NOT_FOUND;

public final class SearchEngine {

  private SearchEngine() { }

  public static int indexOf(ByteBuffer haystack, SearchProcessor processor) {

    while (haystack.hasRemaining()) {
      if (!processor.process(haystack.get())) {
          return haystack.position() - processor.needleLength();
      }
    }

    return -1;
  }

  public static MultiSearchResult indexOfMultiple(ByteBuffer haystack, MultiSearchProcessor processor) {

    final int foundAt = indexOf(haystack, processor);

    return foundAt < 0 ? NOT_FOUND : new MultiSearchResult(foundAt, processor.getFoundNeedleId());
  }

}
