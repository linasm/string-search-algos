package search.algorithm

import search.engine.SearchContext


trait SearchAlgorithm {

  def apply(needle: Array[Byte]): SearchContext

}
