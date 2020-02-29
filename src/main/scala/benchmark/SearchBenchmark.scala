package benchmark

import java.nio.{ByteBuffer, ByteOrder}
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import search.algorithm._


@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(
  value = 1,
  // Requires hsdis-amd64.dylib:
  //jvmArgs = Array("-XX:+UnlockDiagnosticVMOptions", "-XX:CompileCommand=print,*.shiftingBitMask", "-XX:PrintAssemblyOptions=intel")
)
class SearchBenchmark {

  @Param
  var searchInput: SearchInputType = _

  private var needleBytes, haystackBytes: Array[Byte] = _
  private var haystack: ByteBuffer = _

  private var kmpContext: KnuthMorrisPratt = _
  private var shiftingBitMaskContext: ShiftingBitMask = _
  private var ahoCorasicContext: AhoCorasic = _

  @Setup
  def setup(): Unit = {
    haystackBytes = searchInput.getHaystack.getBytes(SearchInput.UsAscii)
    needleBytes   = searchInput.getNeedle.getBytes(SearchInput.UsAscii)

    haystack      = ByteBuffer.wrap(haystackBytes).order(ByteOrder.LITTLE_ENDIAN)

    kmpContext = KnuthMorrisPratt.init(needleBytes)
    shiftingBitMaskContext = ShiftingBitMask.init(needleBytes)
    ahoCorasicContext = AhoCorasic.init(needleBytes)
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def ahoCorasic: Int = {
    haystack.rewind()
    ahoCorasicContext.newProcessor.indexOf(haystack)
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def kmp: Int = {
    haystack.rewind()
    kmpContext.newProcessor.indexOf(haystack)
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def shiftingBitMaskUnrolled: Int = {
    haystack.rewind()
    shiftingBitMaskContext.newProcessor.indexOf(haystack)
  }

}
