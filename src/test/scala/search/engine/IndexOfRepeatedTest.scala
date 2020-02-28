package search.engine

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import search.algorithm.{AhoCorasic, KnuthMorrisPratt, SearchAlgorithm, ShiftingBitMask}

abstract class IndexOfRepeatedTest(algorithm: Array[Byte] => SearchAlgorithm)
  extends AnyWordSpec with Matchers with ByteBufferConverter with IndexOfSupport {

  "find the needle repeatedly" in {

    val haystack = toByteBuffer("abababaabab")

    val processor = algorithm("ba".getBytes).newProcessor

    indexOf(haystack, processor) shouldEqual 1
    indexOf(haystack, processor) shouldEqual 3
    indexOf(haystack, processor) shouldEqual 5
    indexOf(haystack, processor) shouldEqual 8
    indexOf(haystack, processor) shouldEqual -1
  }

  "find the self-overlapping needle repeatedly" in {

    val haystack = toByteBuffer("abcabcabcabc")

    val processor = algorithm("abcab".getBytes).newProcessor

    indexOf(haystack, processor) shouldEqual 0
    indexOf(haystack, processor) shouldEqual 3
    indexOf(haystack, processor) shouldEqual 6
    indexOf(haystack, processor) shouldEqual -1
  }

}

class AhoCorasicIndexOfRepeatedTest extends IndexOfRepeatedTest(AhoCorasic.init(_))

class KmpIndexOfRepeatedTest extends IndexOfRepeatedTest(KnuthMorrisPratt.init)

class ShiftingBitMaskIndexOfRepeatedTest extends IndexOfRepeatedTest(ShiftingBitMask.init)
