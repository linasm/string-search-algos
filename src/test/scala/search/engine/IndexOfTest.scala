package search.engine

import java.util

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.{Gen, Properties, Test}
import search.algorithm.{AhoCorasic, KnuthMorrisPratt, SearchAlgorithm, ShiftingBitMask}

abstract class IndexOfTest(algorithmClass: Class[_], algorithm: Array[Byte] => SearchAlgorithm)
  extends Properties(s"indexOf(${algorithmClass.getSimpleName})")
    with ByteBufferConverter {

  override def overrideParameters(p: Test.Parameters): Test.Parameters = {
      p.withMinSuccessfulTests(10000)
  }

  property("find existing needle in a haystack") = forAll(Gen.nonEmptyListOf(arbitrary[Byte])) { haystackList =>

    val haystack = haystackList.toArray
    val maxIndex = math.max(0, haystack.length - 1)

    forAll(
      Gen.choose(0, maxIndex),
      Gen.choose(0, maxIndex)) { (a, b) =>

      val from = math.min(a, b)
      val until = math.min(from + 64, math.max(a, b) + 1)

      val needle = haystack.slice(from, until)

      val searchProcessor = algorithm(needle).newProcessor

      val indexOf = SearchEngine.indexOf(toByteBuffer(haystack), searchProcessor)

      indexOf <= from &&
        util.Arrays.equals(needle, haystack.slice(indexOf, indexOf + needle.length))
    }
  }

  property("report a missing needle in a haystack") = forAll { (haystackList: List[Byte], needleList: List[Byte]) =>

    val haystack = haystackList.toArray
    val needle = needleList.toArray

    (needle.length <= 64 && haystack.indexOfSlice(needle) == -1) ==> {

      val searchProcessor = algorithm(needle).newProcessor

      SearchEngine.indexOf(toByteBuffer(haystack), searchProcessor) == -1
    }
  }

}

object AhoCorasicIndexOfTest extends IndexOfTest(classOf[AhoCorasic], AhoCorasic.init(_))

object KmpIndexOfTest extends IndexOfTest(classOf[KnuthMorrisPratt], KnuthMorrisPratt.init)

object ShiftingBitMaskIndexOfTest extends IndexOfTest(classOf[ShiftingBitMask], ShiftingBitMask.init)

