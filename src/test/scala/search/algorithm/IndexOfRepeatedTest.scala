package search.algorithm

import java.nio.charset.Charset

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


abstract class IndexOfRepeatedTest(algorithm: Array[Byte] => SearchAlgorithm)
  extends AnyWordSpec with Matchers with ByteBufferConverter {

  private val utf8 = Charset.forName("Utf-8")

  "find the needle repeatedly" in {

    val haystack = toByteBuffer("abababaabab")

    val processor = AhoCorasic.init("ba".getBytes).newProcessor

    processor.indexOf(haystack) shouldEqual 1
    processor.indexOf(haystack) shouldEqual 3
    processor.indexOf(haystack) shouldEqual 5
    processor.indexOf(haystack) shouldEqual 8
    processor.indexOf(haystack) shouldEqual -1
  }

  "find the self-overlapping needle repeatedly" in {

    val haystack = toByteBuffer("abcabcabcabc")

    val processor = AhoCorasic.init("abcab".getBytes).newProcessor

    processor.indexOf(haystack) shouldEqual 0
    processor.indexOf(haystack) shouldEqual 3
    processor.indexOf(haystack) shouldEqual 6
    processor.indexOf(haystack) shouldEqual -1
  }

  "find a long needle" in {

    val haystack = toByteBuffer(Array.tabulate(256)(_.toByte))
    val needle = Array.tabulate(57)(x => (x + 16).toByte)

    val processor = algorithm(needle).newProcessor

    processor.indexOf(haystack) shouldEqual 16
    processor.indexOf(haystack) shouldEqual -1
  }

  "find UTF symbols" in {

    val haystack = toByteBuffer("Šešios žąsys su šešiais žąsyčiais.".getBytes(utf8))

    val processor = algorithm("žąs".getBytes(utf8)).newProcessor

    processor.indexOf(haystack) shouldEqual 9
    processor.indexOf(haystack) shouldEqual 30
    processor.indexOf(haystack) shouldEqual -1
  }

}

class AhoCorasicIndexOfRepeatedTest extends IndexOfRepeatedTest(AhoCorasic.init(_))

class KmpIndexOfRepeatedTest extends IndexOfRepeatedTest(KnuthMorrisPratt.init)

class ShiftingBitMaskIndexOfRepeatedTest extends IndexOfRepeatedTest(ShiftingBitMask.init)
