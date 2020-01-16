package search.engine

trait SearchContext {
  def newProcessor: SearchProcessor
}
