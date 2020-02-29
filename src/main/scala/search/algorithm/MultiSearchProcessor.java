package search.algorithm;

import java.nio.ByteBuffer;

import static search.algorithm.MultiSearchResult.NOT_FOUND;

public interface MultiSearchProcessor extends SearchProcessor {

  int getFoundNeedleId();

  default MultiSearchResult indexOfMultiple(ByteBuffer haystack) {

    final int foundAt = indexOf(haystack);

    return foundAt < 0 ? NOT_FOUND : new MultiSearchResult(foundAt, getFoundNeedleId());
  }

}
