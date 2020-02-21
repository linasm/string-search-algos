package benchmark

import java.util.concurrent.TimeUnit
import java.util.regex

import com.google.common.primitives.Bytes
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
  //jvm = "/Library/Java/JavaVirtualMachines/graalvm-ee-java11-19.3.0.2/Contents/Home/bin/java",
  // Requires hsdis-amd64.dylib in jdk/Contents/Home/bin:
  // jvmArgs = Array("-XX:+UnlockDiagnosticVMOptions", "-XX:CompileCommand=print,*.ahoCorasic", "-XX:PrintAssemblyOptions=intel")
)
class SearchBenchmark {

  @Param
  var searchInput: SearchInputType = _

  private var needleStr, haystackStr: String = _

  private var needleBytes, haystackBytes: Array[Byte] = _

  private var kmpContext, shiftingBitMaskContext, ahoCorasicContext: SearchContext = _

  private var pattern: regex.Pattern = _

  @Setup
  def setup(): Unit = {
    haystackStr = searchInput.getHaystack
    needleStr   = searchInput.getNeedle

    haystackBytes = haystackStr.getBytes(SearchInput.UsAscii)
    needleBytes = needleStr.getBytes(SearchInput.UsAscii)

    kmpContext = KnuthMorrisPratt(needleBytes)
    shiftingBitMaskContext = ShiftingBitMask(needleBytes)
    ahoCorasicContext = AhoCorasic(needleBytes)

    pattern = regex.Pattern.compile(needleStr)
  }

//  @Benchmark
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  def ahoCorasic: Int = SearchEngine.indexOf(haystackBytes, ahoCorasicContext.newProcessor)
//
//  @Benchmark
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  def guavaIndexOf: Int = Bytes.indexOf(haystackBytes, needleBytes)
//
//  @Benchmark
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  def javaIndexOf: Int = haystackStr.indexOf(needleStr)
//
//  @Benchmark
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  def kmp: Int = SearchEngine.indexOf(haystackBytes, kmpContext.newProcessor)
//
//  @Benchmark
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  def regexPattern: Int = {
//    val matcher = pattern.matcher(haystackStr)
//    if (matcher.find()) matcher.start() else -1
//  }
//
//  @Benchmark
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  def scalaIndexOfSlice: Int = haystackBytes.indexOfSlice(needleBytes)
//
  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def shiftingBitMask: Int = SearchEngine.indexOf(haystackBytes, shiftingBitMaskContext.newProcessor)

}
