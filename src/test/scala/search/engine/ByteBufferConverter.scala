package search.engine

import java.nio.{ByteBuffer, ByteOrder}

trait ByteBufferConverter {

  protected def toByteBuffer(bytes: Array[Byte]): ByteBuffer = {
    ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
  }

  protected def toByteBuffer(s: String): ByteBuffer = {
    toByteBuffer(s.getBytes)
  }

}
