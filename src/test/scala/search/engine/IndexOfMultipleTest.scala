package search.engine

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import search.algorithm.{AhoCorasic, MultiSearchAlgorithm}
import search.engine.MultiSearchResult.NOT_FOUND

abstract class IndexOfMultipleTest(
    algorithmClass: Class[_],
    algorithm: Array[Array[Byte]] => MultiSearchAlgorithm)
  extends AnyWordSpec with Matchers {

  import SearchEngine.indexOfMultiple

  s"indexOfMultiple(${algorithm.getClass.getSimpleName})" should {

    "find single needle repeatedly" in {

      val haystack = "aabbaabbaa".getBytes

      val processor = algorithm(Array("aa".getBytes)).newProcessor

      indexOfMultiple(haystack, processor, 0) shouldEqual found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 2) shouldEqual found(foundAt = 4, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 6) shouldEqual found(foundAt = 8, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 10) shouldEqual NOT_FOUND
    }

    "find multiple needles" in {

      val haystack = "aabbccdd".getBytes

      val processor = algorithm(Array("aa".getBytes, "bb".getBytes, "cc".getBytes)).newProcessor

      indexOfMultiple(haystack, processor, 0) shouldEqual found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 2) shouldEqual found(foundAt = 2, foundNeedleId = 1)
      indexOfMultiple(haystack, processor, 4) shouldEqual found(foundAt = 4, foundNeedleId = 2)
      indexOfMultiple(haystack, processor, 6) shouldEqual NOT_FOUND
    }

    "find multiple overlapping needles" in {

      val haystack = "abcd".getBytes

      val processor = algorithm(Array("ab".getBytes, "bc".getBytes, "cd".getBytes)).newProcessor

      indexOfMultiple(haystack, processor, 0) shouldEqual found(foundAt = 0, foundNeedleId = 0)
      indexOfMultiple(haystack, processor, 2) shouldEqual found(foundAt = 1, foundNeedleId = 1)
      indexOfMultiple(haystack, processor, 3) shouldEqual found(foundAt = 2, foundNeedleId = 2)
      indexOfMultiple(haystack, processor, 4) shouldEqual NOT_FOUND
    }

  }

  private def found(foundAt: Int, foundNeedleId: Int) = new MultiSearchResult(foundAt, foundNeedleId)

}

class AhoCorasicIndexOfMultipleTest extends IndexOfMultipleTest(classOf[AhoCorasic], AhoCorasic.init(_: _*))
