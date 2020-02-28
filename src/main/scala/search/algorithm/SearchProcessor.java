package search.algorithm;

public interface SearchProcessor {
  int needleLength();
  boolean process(byte value);
  void reset();
}
