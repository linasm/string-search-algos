import java.nio.{ByteBuffer, ByteOrder}

import search.algorithm.ShiftingBitMask

val haystackBytes = "bababbaabaaba".getBytes

val processor = ShiftingBitMask.init("ba".getBytes).newProcessor

val haystack = ByteBuffer.wrap(haystackBytes).order(ByteOrder.LITTLE_ENDIAN)

haystack.remaining()

processor.indexOf(haystack)
processor.indexOf(haystack)
processor.indexOf(haystack)
processor.indexOf(haystack)
processor.indexOf(haystack)
processor.indexOf(haystack)
