import java.nio.{ByteBuffer, ByteOrder}

import search.algorithm.ShiftingBitMask
import search.engine.SearchEngine

val haystackBytes = "bababbaabaaba".getBytes

val p = ShiftingBitMask.init("ba".getBytes).newProcessor

val haystack = ByteBuffer.wrap(haystackBytes).order(ByteOrder.LITTLE_ENDIAN)

haystack.remaining()

SearchEngine.indexOf(haystack, p)
SearchEngine.indexOf(haystack, p)
SearchEngine.indexOf(haystack, p)
SearchEngine.indexOf(haystack, p)
SearchEngine.indexOf(haystack, p)
SearchEngine.indexOf(haystack, p)
