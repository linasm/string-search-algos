package search.engine

import java.nio.{ByteBuffer, ByteOrder}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import search.algorithm.{AhoCorasic, KnuthMorrisPratt, SearchAlgorithm, ShiftingBitMask}


abstract class IndexOfRepeatedTest(algorithm: SearchAlgorithm) extends AnyWordSpec with Matchers {

  s"indexOf(${algorithm.getClass.getSimpleName})" should {

    "find the needle repeatedly" in {

      val haystack = toByteBuffer("abababaabab")

      val processor = AhoCorasic("ba".getBytes).newProcessor

      indexOf(haystack, processor) shouldEqual 1
      indexOf(haystack, processor) shouldEqual 3
      indexOf(haystack, processor) shouldEqual 5
      indexOf(haystack, processor) shouldEqual 8
      indexOf(haystack, processor) shouldEqual -1
    }

    "find the self-overlapping needle repeatedly" in {

      val haystack = toByteBuffer("abcabcabcabc")

      val processor = AhoCorasic("abcab".getBytes).newProcessor

      indexOf(haystack, processor) shouldEqual 0
      indexOf(haystack, processor) shouldEqual 3
      indexOf(haystack, processor) shouldEqual 6
      indexOf(haystack, processor) shouldEqual -1
    }

  }

  private def toByteBuffer(s: String) = ByteBuffer.wrap(s.getBytes).order(ByteOrder.LITTLE_ENDIAN)

  private def indexOf(haystack: ByteBuffer, searchProcessor: SearchProcessor): Int = {

    searchProcessor match {
      case unrolledSearchProcessor: UnrolledSearchProcessor =>
        NioSearchEngine.indexOf(haystack, unrolledSearchProcessor)
      case _ =>
        NioSearchEngine.indexOf(haystack, searchProcessor)
    }
  }

}

class AhoCorasicIndexOfRepeatedTest extends IndexOfRepeatedTest(AhoCorasic)

class KmpIndexOfRepeatedTest extends IndexOfRepeatedTest(KnuthMorrisPratt)

class ShiftingBitMaskIndexOfRepeatedTest extends IndexOfRepeatedTest(ShiftingBitMask)
