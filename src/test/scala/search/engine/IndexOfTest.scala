package search.engine

import java.nio.{ByteBuffer, ByteOrder}
import java.util

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.{Gen, Properties, Test}
import search.algorithm.{AhoCorasic, KnuthMorrisPratt, SearchAlgorithm, ShiftingBitMask}


abstract class IndexOfTest(algorithm: SearchAlgorithm)
  extends Properties(s"indexOf(${algorithm.getClass.getSimpleName})") {

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
      val until = math.min(from + 57, math.max(a, b) + 1)

      val needle = haystack.slice(from, until)

      val searchProcessor = algorithm(needle).newProcessor

      val index = indexOf(haystack, searchProcessor)

      index <= from &&
        util.Arrays.equals(needle, haystack.slice(index, index + needle.length))
    }
  }

  property("report a missing needle in a haystack") = forAll { (haystackList: List[Byte], needleList: List[Byte]) =>

    val haystack = haystackList.toArray
    val needle = needleList.toArray

    (needle.length <= 57 && haystack.indexOfSlice(needle) == -1) ==> {

      val searchProcessor = algorithm(needle).newProcessor

      indexOf(haystack, searchProcessor) == -1
    }
  }

  private def indexOf(haystack: Array[Byte], searchProcessor: SearchProcessor): Int = {
    searchProcessor match {
      case unrolledSearchProcessor: UnrolledSearchProcessor =>
        SearchEngine.indexOf(ByteBuffer.wrap(haystack).order(ByteOrder.LITTLE_ENDIAN), unrolledSearchProcessor)
      case _ =>
        SearchEngine.indexOf(haystack, searchProcessor)
    }
  }

}

object KmpIndexOfTest extends IndexOfTest(KnuthMorrisPratt)

object ShiftingBitMaskIndexOfTest extends IndexOfTest(ShiftingBitMask)

object AhoCorasicIndexOfTest extends IndexOfTest(AhoCorasic)
