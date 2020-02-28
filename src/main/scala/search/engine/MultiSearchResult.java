package search.engine;

import java.util.Objects;

public final class MultiSearchResult {

  final static MultiSearchResult NOT_FOUND = new MultiSearchResult(-1, -1);

  private final int foundAt, foundNeedleId;

  MultiSearchResult(int foundAt, int foundNeedleId) {
    this.foundAt = foundAt;
    this.foundNeedleId = foundNeedleId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MultiSearchResult that = (MultiSearchResult) o;
    return foundAt == that.foundAt &&
        foundNeedleId == that.foundNeedleId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(foundAt, foundNeedleId);
  }

}
