package benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import search.engine._


@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(1)
class SearchBenchmark {

  @Param
  var algorithm: Algorithm = _

  private var searchContext: SearchContext = _
  private var haystack: Array[Byte] = _

  @Setup
  def setup(): Unit = {
    haystack = ("A" * 1000000 + "B").getBytes()

    val needle = ("A" * 63 + "B").getBytes()
    searchContext = algorithm.newContext(needle)
  }

  @Benchmark
  def search(): Int = {
    SearchEngine.indexOf(haystack, searchContext.newProcessor)
  }

}
