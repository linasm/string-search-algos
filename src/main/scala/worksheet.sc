import java.nio.{ByteBuffer, ByteOrder}

import search.algorithm.ShiftingBitMask
import search.engine.NioSearchEngine

val haystackBytes = "abababababab".getBytes

val p = ShiftingBitMask("ba".getBytes).newProcessor

val haystack = ByteBuffer.wrap(haystackBytes).order(ByteOrder.LITTLE_ENDIAN)

haystack.remaining()

NioSearchEngine.indexOf(haystack, p)
NioSearchEngine.indexOf(haystack, p)
NioSearchEngine.indexOf(haystack, p)
NioSearchEngine.indexOf(haystack, p)
NioSearchEngine.indexOf(haystack, p)
NioSearchEngine.indexOf(haystack, p)
