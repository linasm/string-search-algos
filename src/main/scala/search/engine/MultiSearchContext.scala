package search.engine

trait MultiSearchContext extends SearchContext {
  def newProcessor: MultiSearchProcessor
}
