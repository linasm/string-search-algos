package search.engine

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import search.algorithm.{AhoCorasic, MultiSearchAlgorithm}
import search.engine.SearchEngine.{Found, NotFound}

abstract class IndexOfMultipleTest(algorithm: MultiSearchAlgorithm) extends AnyWordSpec with Matchers {

  import SearchEngine.indexOfMultiple

  s"indexOfMultiple(${algorithm.getClass.getSimpleName})" should {

    "find singe needle repeatedly" in {

      val haystack = "aabbaabbaa".getBytes

      val processor = AhoCorasic("aa".getBytes).newProcessor

      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 2) shouldEqual Found(foundAt = 4, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 6) shouldEqual Found(foundAt = 8, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 10) shouldEqual NotFound
    }

    "find multiple needles" in {

      val haystack = "aabbccdd".getBytes

      val processor = AhoCorasic("aa".getBytes, "bb".getBytes, "cc".getBytes).newProcessor

      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 2) shouldEqual Found(foundAt = 2, foundNeedleId = 1)
      indexOfMultiple(haystack, processor, 4) shouldEqual Found(foundAt = 4, foundNeedleId = 2)
      indexOfMultiple(haystack, processor, 6) shouldEqual NotFound
    }

    "find multiple overlapping needles" in {

      val haystack = "abcd".getBytes

      val processor = AhoCorasic("ab".getBytes, "bc".getBytes, "cd".getBytes).newProcessor

      indexOfMultiple(haystack, processor) shouldEqual Found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 2) shouldEqual Found(foundAt = 1, foundNeedleId = 1)
      indexOfMultiple(haystack, processor, 3) shouldEqual Found(foundAt = 2, foundNeedleId = 2)
      indexOfMultiple(haystack, processor, 4) shouldEqual NotFound
    }

  }

}

class AhoCorasicIndexOfMultipleTest extends IndexOfMultipleTest(AhoCorasic)
