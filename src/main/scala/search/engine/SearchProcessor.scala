package search.engine

trait SearchProcessor {
  def needleLength: Int
  def process(value: Byte): Boolean
  def reset(): Unit
}
