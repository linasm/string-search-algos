val needle = "cad".getBytes()
val haystack = "abracadabra".getBytes()

val context = search.algorithm.ShiftingBitMask(needle)
val processor = context.newProcessor

search.engine.SearchEngine.indexOf(haystack, processor)
