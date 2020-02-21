package search.engine

trait UnrolledSearchProcessor extends SearchProcessor {

  def processUnrolled(value: Long): Int

}
