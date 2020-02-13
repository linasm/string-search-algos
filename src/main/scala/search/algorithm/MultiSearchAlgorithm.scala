package search.algorithm

import search.engine.{MultiSearchContext, SearchContext}

import scala.annotation.varargs


trait MultiSearchAlgorithm extends SearchAlgorithm {

  @varargs
  def apply(needles: Array[Byte]*): MultiSearchContext

  override def apply(needle: Array[Byte]): SearchContext = apply(Seq(needle): _*)

}
