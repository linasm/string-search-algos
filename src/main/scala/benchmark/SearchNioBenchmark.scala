package benchmark

import java.nio.charset.Charset
import java.nio.{ByteBuffer, ByteOrder}
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import search.algorithm._
import search.engine._


@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(
  value = 1,
//  jvm = "/Library/Java/JavaVirtualMachines/graalvm-ee-java11-19.3.0.2/Contents/Home/bin/java",
  jvm = "/Library/Java/JavaVirtualMachines/graalvm-ce-java11-20.0.0/Contents/Home/bin/java",
  // Requires hsdis-amd64.dylib in jdk/Contents/Home/bin:
  jvmArgs = Array("-Xmx8G")//, "-XX:+UnlockDiagnosticVMOptions", "-XX:CompileCommand=print,*.shiftingBitMask", "-XX:PrintAssemblyOptions=intel")
)
class SearchNioBenchmark {

  @Param
  var searchInput: SearchInputType = _

  private var needleBytes, haystackBytes: Array[Byte] = _
  private var haystack: ByteBuffer = _

  private var kmpContext, shiftingBitMaskContext, ahoCorasicContext: SearchContext = _

  @Setup
  def setup(): Unit = {
    haystackBytes = searchInput.getHaystack.getBytes(SearchInput.UsAscii)
    needleBytes   = searchInput.getNeedle.getBytes(SearchInput.UsAscii)
    haystack      = ByteBuffer.wrap(haystackBytes).order(ByteOrder.LITTLE_ENDIAN)

    kmpContext = KnuthMorrisPratt(needleBytes)
    shiftingBitMaskContext = ShiftingBitMask(needleBytes)
    ahoCorasicContext = AhoCorasic(needleBytes)
  }

//  @Benchmark
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  def ahoCorasic: Int = SearchEngine.indexOf(haystackBytes, ahoCorasicContext.newProcessor)
//
//  @Benchmark
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  def kmp: Int = SearchEngine.indexOf(haystackBytes, kmpContext.newProcessor)

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def shiftingBitMask: Int = {
    haystack.rewind()
    SearchEngine.indexOf(haystack, shiftingBitMaskContext.newProcessor.asInstanceOf[UnrolledSearchProcessor])
  }

}


