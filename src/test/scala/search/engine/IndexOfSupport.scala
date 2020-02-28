package search.engine

import java.nio.ByteBuffer

import search.algorithm.{SearchProcessor, UnrolledSearchProcessor}


trait IndexOfSupport {

  protected def indexOf(haystack: ByteBuffer, searchProcessor: SearchProcessor): Int = {

    searchProcessor match {

      case unrolledSearchProcessor: UnrolledSearchProcessor =>
        SearchEngine.indexOf(haystack, unrolledSearchProcessor)

      case _ =>
        SearchEngine.indexOf(haystack, searchProcessor)

    }
  }

}
