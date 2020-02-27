package search.engine

trait UnrolledSearchProcessor extends SearchProcessor {

  def processUnrolled(value: Long): Boolean

  def hasPreviouslyFound: Boolean

  def nextOffset(): Int

}
