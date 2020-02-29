package search.algorithm;

import java.nio.ByteBuffer;

public interface SearchProcessor {

  int needleLength();

  boolean process(byte value);

  void reset();

  default int indexOf(ByteBuffer haystack) {

    while (haystack.hasRemaining()) {
      if (!process(haystack.get())) {
        return haystack.position() - needleLength();
      }
    }

    return -1;
  }

}
