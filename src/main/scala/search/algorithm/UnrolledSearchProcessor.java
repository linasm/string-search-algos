package search.algorithm;

import com.google.common.base.Preconditions;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

public interface UnrolledSearchProcessor extends SearchProcessor {

  boolean process(long value);

  boolean hasPreviouslyFound();

  int nextOffset();

  default int indexOf(ByteBuffer haystack) {

    Preconditions.checkArgument(
        haystack.order().equals(ByteOrder.LITTLE_ENDIAN),
        "LITTLE_ENDIAN buffer required");

    if (hasPreviouslyFound()) {
      return haystack.position() - nextOffset();
    }

    while (haystack.remaining() >= 8) {
      if (!process(haystack.getLong())) {
        return haystack.position() - nextOffset();
      }
    }

    return SearchProcessor.super.indexOf(haystack);
  }

  default int indexOf(LongBuffer haystack) {

    Preconditions.checkArgument(
        haystack.order().equals(ByteOrder.LITTLE_ENDIAN),
        "LITTLE_ENDIAN buffer required");

    if (hasPreviouslyFound()) {
      return haystack.position() - nextOffset();
    }

    while (haystack.remaining() >= 8) {
      if (!process(haystack.get())) {
        return haystack.position() - nextOffset();
      }
    }

    return -1;
  }

}
