import search.algorithm._
import search.engine._

val needle = "cad".getBytes()
val needle2 = "r".getBytes()
val haystack = "abracadabra".getBytes()

val context = search.algorithm.AhoCorasic(needle, needle2)
val processor = context.newProcessor

SearchEngine.indexOfMultiple(haystack, processor)
