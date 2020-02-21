import java.nio.charset.Charset
import java.nio.{ByteBuffer, ByteOrder}

import benchmark.SearchInput
import search.algorithm.ShiftingBitMask
import search.engine.SearchEngine

val haystackBytes = "abcdefghij".getBytes

val p = ShiftingBitMask("ij".getBytes).newProcessor

val haystack = ByteBuffer.wrap(haystackBytes).order(ByteOrder.LITTLE_ENDIAN)

haystack.remaining()

SearchEngine.indexOf(haystack, p)

Integer.toUnsignedLong(-1073742256)

SearchInput.Hamlet.getBytes(Charset.forName("us-ascii")).length

1024 * 1024 * 1024 / SearchInput.Hamlet.length