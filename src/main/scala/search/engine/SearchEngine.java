package search.engine;

import com.google.common.base.Preconditions;
import search.algorithm.MultiSearchProcessor;
import search.algorithm.SearchProcessor;
import search.algorithm.UnrolledSearchProcessor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

import static search.engine.MultiSearchResult.NOT_FOUND;

public final class SearchEngine {

  private SearchEngine() { }

  public static int indexOf(ByteBuffer haystack, UnrolledSearchProcessor processor) {

    Preconditions.checkArgument(haystack.order().equals(ByteOrder.LITTLE_ENDIAN));

    if (processor.hasPreviouslyFound()) {
      return haystack.position() - processor.nextOffset();
    }

    while (haystack.remaining() >= 8) {
      if (!processor.process(haystack.getLong())) {
        return haystack.position() - processor.nextOffset();
      }
    }

    while (haystack.hasRemaining()) {
      if (!processor.process(haystack.get())) {
        return haystack.position() - processor.needleLength();
      }
    }

    return -1;
  }

  public static int indexOf(LongBuffer haystack, UnrolledSearchProcessor processor) {

    Preconditions.checkArgument(haystack.order().equals(ByteOrder.LITTLE_ENDIAN));

    if (processor.hasPreviouslyFound()) {
      return haystack.position() - processor.nextOffset();
    }

    while (haystack.remaining() >= 8) {
      if (!processor.process(haystack.get())) {
        return haystack.position() - processor.nextOffset();
      }
    }

    return -1;
  }

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
