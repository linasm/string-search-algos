package search.algorithm

import search.engine.MultiSearchContext

import scala.annotation.varargs


trait MultiSearchAlgorithm extends SearchAlgorithm {

  @varargs
  def apply(needles: Array[Byte]*): MultiSearchContext

}
