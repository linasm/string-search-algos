package benchmark;

public enum SearchInputType {

  REGULAR {
    @Override
    String getNeedle() {
      return "not there";
    }

    @Override
    String getHaystack() {
      return SearchInput$.MODULE$.Hamlet();
    }
  },

  WORST_CASE {
    @Override
    String getNeedle() {
      return "A".repeat(63) + "B";
    }

    @Override
    String getHaystack() {
      return "A".repeat(1489) + "B";
    }
  };

  abstract String getNeedle();

  abstract String getHaystack();

}
