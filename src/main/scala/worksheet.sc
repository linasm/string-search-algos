import java.util.regex.Pattern

import search.algorithm.AhoCorasic
import search.engine.SearchEngine

val needle = "A" * 500000 + "B"
val haystack = "A" * 1000000 + "B"

// slow! haystack.indexOf(needle)

haystack.indexOfSlice(needle) // fast (Knuth-Morris-Pratt, Scala standard library)

val matcher = Pattern.compile(needle).matcher(haystack)
matcher.find()
matcher.start()

SearchEngine.indexOfMultiple("abc".getBytes, AhoCorasic("x".getBytes).newProcessor)