package search.engine

import java.nio.{ByteBuffer, ByteOrder}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import search.algorithm.{AhoCorasic, MultiSearchAlgorithm}
import search.engine.NioSearchEngine.{Found, NotFound}


abstract class IndexOfMultipleTest(algorithm: MultiSearchAlgorithm) extends AnyWordSpec with Matchers {

  import NioSearchEngine.indexOfMultiple

  s"indexOfMultiple(${algorithm.getClass.getSimpleName})" should {

    "find single needle repeatedly" in {

      val haystack = toByteBuffer("aabbaabbaa")

      val processor = AhoCorasic("aa".getBytes).newProcessor

      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 4, foundNeedleId = 0)
      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 8, foundNeedleId = 0)
      indexOfMultiple(haystack, processor) shouldEqual NotFound
    }

    "find multiple needles" in {

      val haystack = toByteBuffer("aabbccdd")

      val processor = AhoCorasic("aa".getBytes, "bb".getBytes, "cc".getBytes).newProcessor

      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 2, foundNeedleId = 1)
      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 4, foundNeedleId = 2)
      indexOfMultiple(haystack, processor) shouldEqual NotFound
    }

    "find multiple overlapping needles" in {

      val haystack = toByteBuffer("abcd")

      val processor = AhoCorasic("ab".getBytes, "bc".getBytes, "cd".getBytes).newProcessor

      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 1, foundNeedleId = 1)
      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 2, foundNeedleId = 2)
      indexOfMultiple(haystack, processor) shouldEqual NotFound
    }

  }

  private def toByteBuffer(s: String): ByteBuffer = ByteBuffer.wrap(s.getBytes).order(ByteOrder.LITTLE_ENDIAN)

}

class AhoCorasicIndexOfMultipleTest extends IndexOfMultipleTest(AhoCorasic)
