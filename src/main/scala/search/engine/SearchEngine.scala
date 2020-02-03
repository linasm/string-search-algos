package search.engine

object SearchEngine {

  sealed trait MultiSearchResult
  case class Found(foundAt: Int, foundNeedleId: Int) extends MultiSearchResult
  case object NotFound extends MultiSearchResult

  def indexOf(haystack: Array[Byte], processor: SearchProcessor, from: Int = 0): Int = {

    val matchAt = indexOfImpl(haystack, processor, from)

    if (matchAt < 0) -1
    else matchAt - processor.needleLength + 1
  }

  def indexOfMultiple(haystack: Array[Byte], processor: MultiSearchProcessor, from: Int = 0): MultiSearchResult = {

    val matchAt = indexOfImpl(haystack, processor, from)

    if (matchAt < 0) NotFound
    else Found(
      foundAt = matchAt - processor.needleLength + 1,
      foundNeedleId = processor.getFoundNeedleId)
  }

  private def indexOfImpl(haystack: Array[Byte], processor: SearchProcessor, from: Int): Int = {

    var i = from max 0

    while (i < haystack.length) {
      if (!processor.process(haystack(i))) return i
      i += 1
    }

    -1
  }

}
