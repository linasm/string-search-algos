package benchmark;

import scala.collection.immutable.Seq;
import search.algorithm.AhoCorasic;
import search.algorithm.KnuthMorrisPratt;
import search.algorithm.ShiftingBitMask;
import search.engine.SearchContext;


public enum Algorithm {

  KNUTH_MORRIS_PRATT {
    @Override
    SearchContext newContext(byte[] needle) {
      return KnuthMorrisPratt.apply(needle);
    }
  },

  SHIFTING_BIT_MASK {
    @Override
    SearchContext newContext(byte[] needle) {
      return ShiftingBitMask.apply(needle);
    }
  },

  AHO_CORASIC {
    @Override
    SearchContext newContext(byte[] needle) {
      return AhoCorasic.apply(needle);
    }
  };

  abstract SearchContext newContext(byte[] needle);
}
