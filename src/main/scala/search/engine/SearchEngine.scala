package search.engine

object SearchEngine {

  def indexOf(haystack: Array[Byte], processor: SearchProcessor, from: Int = 0): Int = {

    var i = from max 0

    while (i < haystack.length) {
      if (!processor.process(haystack(i))) return i - processor.needleLength + 1
      i += 1
    }

    -1
  }

}
