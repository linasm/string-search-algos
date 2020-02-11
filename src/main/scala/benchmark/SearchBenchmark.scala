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
  // Requires hsdis-amd64.dylib in jdk/Contents/Home/bin:
  // jvmArgs = Array("-XX:+UnlockDiagnosticVMOptions", "-XX:CompileCommand=print,*.ahoCorasic", "-XX:PrintAssemblyOptions=intel")
)
class SearchBenchmark {

  import SearchBenchmark._
  import SearchInput._

  @Param
  var searchInput: SearchInput = _

  private var needleStr, haystackStr: String = _

  private var needleBytes, haystackBytes: Array[Byte] = _

  private var kmpContext, shiftingBitMaskContext, ahoCorasicContext: SearchContext = _

  private var pattern: regex.Pattern = _

  @Setup
  def setup(): Unit = {
    val (haystack, needle) = searchInput match {
      case WORST_CASE =>
        ("A" * 1489 + "B", "A" * 63 + "B")
      case REGULAR =>
        (Hamlet, "not there")
    }

    haystackStr = haystack
    needleStr = needle
    haystackBytes = haystack.getBytes()
    needleBytes = needle.getBytes()

    kmpContext = KnuthMorrisPratt(needleBytes)
    shiftingBitMaskContext = ShiftingBitMask(needleBytes)
    ahoCorasicContext = AhoCorasic(needleBytes)

    pattern = regex.Pattern.compile(needleStr)
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def ahoCorasic: Int = SearchEngine.indexOf(haystackBytes, ahoCorasicContext.newProcessor)

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def guavaIndexOf: Int = Bytes.indexOf(haystackBytes, needleBytes)

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def javaIndexOf: Int = haystackStr.indexOf(needleStr)

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def kmp: Int = SearchEngine.indexOf(haystackBytes, kmpContext.newProcessor)

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def regexPattern: Int = {
    val matcher = pattern.matcher(haystackStr)
    if (matcher.find()) matcher.start() else -1
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def scalaIndexOfSlice: Int = haystackBytes.indexOfSlice(needleBytes)

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def shiftingBitMask: Int = SearchEngine.indexOf(haystackBytes, shiftingBitMaskContext.newProcessor)

}

object SearchBenchmark {

  private val Hamlet = """
      |To be or not to be—that is the question:
      |Whether ’tis nobler in the mind to suffer
      |The slings and arrows of outrageous fortune,
      |Or to take arms against a sea of troubles
      |And, by opposing, end them. To die, to sleep—
      |No more—and by a sleep to say we end
      |The heartache and the thousand natural shocks
      |That flesh is heir to—’tis a consummation
      |Devoutly to be wished. To die, to sleep—
      |To sleep, perchance to dream. Ay, there’s the rub,
      |For in that sleep of death what dreams may come,
      |When we have shuffled off this mortal coil,
      |Must give us pause. There’s the respect
      |That makes calamity of so long life.
      |For who would bear the whips and scorns of time,
      |Th’ oppressor’s wrong, the proud man’s contumely,
      |The pangs of despised love, the law’s delay,
      |The insolence of office, and the spurns
      |That patient merit of th’ unworthy takes,
      |When he himself might his quietus make
      |With a bare bodkin? Who would fardels bear,
      |To grunt and sweat under a weary life,
      |But that the dread of something after death,
      |The undiscovered country from whose bourn
      |No traveler returns, puzzles the will
      |And makes us rather bear those ills we have
      |Than fly to others that we know not of?
      |Thus conscience does make cowards (of us all,)
      |And thus the native hue of resolution
      |Is <sicklied> o’er with the pale cast of thought,
      |And enterprises of great pitch and moment
      |With this regard their currents turn awry
      |And lose the name of action.—Soft you now,
      |The fair Ophelia.—Nymph, in thy orisons
      |Be all my sins remembered.
      |""".stripMargin

}
