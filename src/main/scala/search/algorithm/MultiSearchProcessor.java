package search.algorithm;

import java.nio.ByteBuffer;

public interface MultiSearchProcessor extends SearchProcessor {

  int getFoundNeedleId();

  default MultiSearchResult indexOfMultiple(ByteBuffer haystack) {

    final int foundAt = indexOf(haystack);

    return foundAt < 0 ? MultiSearchResult.NOT_FOUND : new MultiSearchResult(foundAt, getFoundNeedleId());
  }

}
