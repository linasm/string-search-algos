package benchmark;

public enum SearchInputType {
//  REGULAR {
//    @Override
//    String getNeedle() {
//      return "not there";
//    }
//    @OverrideSearchNioBenchmark.scala
//    String getHaystack() {
//      return SearchInput$.MODULE$.Hamlet();
//    }
//  },
  ONE_MIB {
    private String s() {
      return SearchInput$.MODULE$.Hamlet().repeat(1024 * 1024 / SearchInput$.MODULE$.Hamlet().length());
    }
    @Override
    String getNeedle() {
      return "not there";
    }
    @Override
    String getHaystack() {
      return s();
    }
//  },
//  WORST_CASE {
//    @Override
//    String getNeedle() {
//      return "A".repeat(63) + "B";
//    }
//
//    @Override
//    String getHaystack() {
//      return "A".repeat(1489) + "B";
//    }
  };
  abstract String getNeedle();
  abstract String getHaystack();
}
