package search.engine

import java.util

import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.{Gen, Properties, Test}
import search.algorithm.{AhoCorasic, KnuthMorrisPratt, SearchAlgorithm, ShiftingBitMask}

abstract class IndexOfTest(algorithm: SearchAlgorithm)
  extends Properties(s"indexOf(${algorithm.getClass.getSimpleName})") {

  override def overrideParameters(p: Test.Parameters): Test.Parameters = {
      p.withMinSuccessfulTests(10000)
  }

  property("find existing needle in a haystack") = forAll { haystack: Array[Byte] =>

    val maxIndex = math.max(0, haystack.length - 1)

    forAll(
      Gen.choose(0, maxIndex),
      Gen.choose(0, maxIndex)) { (a, b) =>

      val from = math.min(a, b)
      val until = math.min(from + 64, math.max(a, b) + 1)

      val needle = haystack.slice(from, until)

      val searchProcessor = algorithm(needle).newProcessor

      val indexOf = SearchEngine.indexOf(haystack, searchProcessor)

      indexOf <= from &&
        util.Arrays.equals(needle, haystack.slice(indexOf, indexOf + needle.length))
    }
  }

  property("report a missing needle in a haystack") = forAll { (haystack: Array[Byte], needle: Array[Byte]) =>

    (needle.length <= 64 && haystack.indexOfSlice(needle) == -1) ==> {

      val searchProcessor = algorithm(needle).newProcessor

      SearchEngine.indexOf(haystack, searchProcessor) == -1
    }
  }

}

object KmpIndexOfTest extends IndexOfTest(KnuthMorrisPratt)

object ShiftingBitMaskIndexOfTest extends IndexOfTest(ShiftingBitMask)

object AhoCorasicIndexOfTest extends IndexOfTest(AhoCorasic)
