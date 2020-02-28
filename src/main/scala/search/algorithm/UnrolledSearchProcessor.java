package search.algorithm;

public interface UnrolledSearchProcessor extends SearchProcessor {
  boolean process(long value);
  boolean hasPreviouslyFound();
  int nextOffset();
}
