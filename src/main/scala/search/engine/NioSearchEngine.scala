package search.engine

import java.nio.{ByteBuffer, ByteOrder, LongBuffer}

object NioSearchEngine {

  sealed trait MultiSearchResult
  case class Found(foundAt: Int, foundNeedleId: Int) extends MultiSearchResult
  case object NotFound extends MultiSearchResult

  def indexOf(haystack: ByteBuffer, processor: UnrolledSearchProcessor): Int = {

    require(haystack.order == ByteOrder.LITTLE_ENDIAN)

    if (processor.hasPreviouslyFound) {
      return haystack.position() - processor.nextOffset()
    }

    while (haystack.remaining >= 8) {
      if (!processor.processUnrolled(haystack.getLong)) {
        return haystack.position() - processor.nextOffset()
      }
    }

    while (haystack.hasRemaining) {
      if (!processor.process(haystack.get)) {
        return haystack.position() - processor.needleLength
      }
    }

    -1
  }

  def indexOf(haystack: LongBuffer, processor: UnrolledSearchProcessor): Int = {

    require(haystack.order == ByteOrder.LITTLE_ENDIAN)

    if (processor.hasPreviouslyFound) {
      return haystack.position() - processor.nextOffset()
    }

    while (haystack.remaining >= 8) {
      if (!processor.processUnrolled(haystack.get)) {
        return haystack.position() - processor.nextOffset()
      }
    }

    -1
  }

  def indexOf(haystack: ByteBuffer, processor: SearchProcessor): Int = {

    while (haystack.hasRemaining) {
      if (!processor.process(haystack.get)) return {
        haystack.position() - processor.needleLength
      }
    }

    -1
  }

  def indexOfMultiple(haystack: ByteBuffer, processor: MultiSearchProcessor): MultiSearchResult = {

    val foundAt = indexOf(haystack, processor)

    if (foundAt < 0) NotFound
    else Found(
      foundAt = foundAt,
      foundNeedleId = processor.getFoundNeedleId)
  }

}
