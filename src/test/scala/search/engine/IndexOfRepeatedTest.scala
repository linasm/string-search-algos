package search.engine

import java.nio.charset.Charset

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import search.algorithm.{AhoCorasic, KnuthMorrisPratt, SearchAlgorithm, ShiftingBitMask}

abstract class IndexOfRepeatedTest(algorithm: Array[Byte] => SearchAlgorithm)
  extends AnyWordSpec with Matchers with ByteBufferConverter with IndexOfSupport {

  private val utf8 = Charset.forName("Utf-8")

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

  "find a long needle" in {

    val haystack = toByteBuffer(Array.tabulate(256)(_.toByte))
    val needle = Array.tabulate(57)(x => (x + 16).toByte)

    val processor = algorithm(needle).newProcessor

    indexOf(haystack, processor) shouldEqual 16
    indexOf(haystack, processor) shouldEqual -1
  }

  "find UTF symbols" in {

    val haystack = toByteBuffer("Šešios žąsys su šešiais žąsyčiais.".getBytes(utf8))

    val processor = algorithm("žąs".getBytes(utf8)).newProcessor

    indexOf(haystack, processor) shouldEqual 9
    indexOf(haystack, processor) shouldEqual 30
    indexOf(haystack, processor) shouldEqual -1
  }

}

class AhoCorasicIndexOfRepeatedTest extends IndexOfRepeatedTest(AhoCorasic.init(_))

class KmpIndexOfRepeatedTest extends IndexOfRepeatedTest(KnuthMorrisPratt.init)

class ShiftingBitMaskIndexOfRepeatedTest extends IndexOfRepeatedTest(ShiftingBitMask.init)
