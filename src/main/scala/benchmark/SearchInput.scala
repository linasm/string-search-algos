package benchmark

import java.nio.{ByteBuffer, ByteOrder}

import com.google.common.io.Resources


object SearchInput {

  lazy val Haystack: ByteBuffer = {
    val url = Resources.getResource("en.wikipedia.org_wiki_String-searching_algorithm.html")
    val haystackBytes = Resources.toByteArray(url)
    val haystack = ByteBuffer.allocate(1_000_000_000).order(ByteOrder.LITTLE_ENDIAN)
    for (_ <- 1 to haystack.capacity / haystackBytes.length) {
      haystack.put(haystackBytes)
    }
    haystack.put(haystackBytes.take(haystack.capacity % haystackBytes.length))

    haystack
  }

}
