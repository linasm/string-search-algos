package search.engine

trait MultiSearchProcessor extends SearchProcessor {
  def getFoundNeedleId: Int
}
