Code for my blog posts on string search algorithms:
1. [Beating Textbook Algorithms in String Search](https://medium.com/wix-engineering/beating-textbook-algorithms-in-string-search-5d24b2f1bbd0) - branch [blog-post-1](https://github.com/linasm/string-search-algos/tree/blog-post-1)
2. [Can You Optimise These 2 Lines of Code?](https://medium.com/wix-engineering/can-you-optimise-these-2-lines-of-code-633dd81b1862) - branch [blog-post-2](https://github.com/linasm/string-search-algos/tree/blog-post-2)
3. [An Interesting Case of Loop Unrolling](https://medium.com/wix-engineering/an-interesting-case-of-loop-unrolling-8ea04cf08959) - branch [blog-post-3](https://github.com/linasm/string-search-algos/tree/blog-post-3)

Requires Java 8 or higher (recommended: [GraalVM (20.0.0 CE, based on OpenJDK 11)](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-20.0.0)).
1. Download / install SBT: https://www.scala-sbt.org/download.html
2. Clone this repository: `git clone git@github.com:linasm/string-search-algos.git`
3. `cd string-search-algos`
4. `sbt`
5. From SBT console: `jmh:run` to run the benchmarks
