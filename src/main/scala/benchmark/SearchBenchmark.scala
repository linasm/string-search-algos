package benchmark

import java.nio.charset.Charset
import java.nio.{ByteBuffer, ByteOrder}
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import search.algorithm._


@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(
  value = 1,
  // Requires hsdis-amd64.dylib:
  //jvmArgs = Array("-XX:+UnlockDiagnosticVMOptions", "-XX:CompileCommand=print,*.shiftingBitMask", "-XX:PrintAssemblyOptions=intel")
)
class SearchBenchmark {

  private var needleBytes: Array[Byte] = _
  private var haystack: ByteBuffer = _

  private var kmpContext: KnuthMorrisPratt = _
  private var shiftingBitMaskContext: ShiftingBitMask = _
  private var ahoCorasicContext: AhoCorasic = _

  @Setup
  def setup(): Unit = {

    haystack = SearchInput.Haystack
    needleBytes = "This text will not be found.".getBytes(Charset.forName("us-ascii"))

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
