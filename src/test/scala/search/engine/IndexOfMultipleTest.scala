package search.engine

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import search.algorithm.{AhoCorasic, MultiSearchAlgorithm}
import search.engine.MultiSearchResult.NOT_FOUND

abstract class IndexOfMultipleTest(algorithm: Array[Array[Byte]] => MultiSearchAlgorithm)
  extends AnyWordSpec with Matchers with ByteBufferConverter {

  import SearchEngine.indexOfMultiple

  "find single needle repeatedly" in {

    val haystack = toByteBuffer("aabbaabbaa")

    val processor = algorithm(Array("aa".getBytes)).newProcessor

    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 0, foundNeedleId = 0)
    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 4, foundNeedleId = 0)
    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 8, foundNeedleId = 0)
    indexOfMultiple(haystack, processor) shouldEqual NOT_FOUND
  }

  "find multiple needles" in {

    val haystack = toByteBuffer("aabbccdd")

    val processor = algorithm(Array("aa".getBytes, "bb".getBytes, "cc".getBytes)).newProcessor

    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 0, foundNeedleId = 0)
    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 2, foundNeedleId = 1)
    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 4, foundNeedleId = 2)
    indexOfMultiple(haystack, processor) shouldEqual NOT_FOUND
  }

  "find multiple overlapping needles" in {

    val haystack = toByteBuffer("abcd")

    val processor = algorithm(Array("ab".getBytes, "bc".getBytes, "cd".getBytes)).newProcessor

    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 0, foundNeedleId = 0)
    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 1, foundNeedleId = 1)
    indexOfMultiple(haystack, processor) shouldEqual found(foundAt = 2, foundNeedleId = 2)
    indexOfMultiple(haystack, processor) shouldEqual NOT_FOUND
  }

  private def found(foundAt: Int, foundNeedleId: Int) = new MultiSearchResult(foundAt, foundNeedleId)

}

class AhoCorasicIndexOfMultipleTest extends IndexOfMultipleTest(AhoCorasic.init(_: _*))
