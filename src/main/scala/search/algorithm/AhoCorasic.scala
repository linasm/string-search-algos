package search.algorithm

import java.lang.Byte.toUnsignedInt

import search.engine.{MultiSearchContext, MultiSearchProcessor}

import scala.annotation.varargs
import scala.collection.mutable


object AhoCorasic extends MultiSearchAlgorithm {

  final class Processor(trieRoot: TrieNode, needleLengths: Array[Int]) extends MultiSearchProcessor {

    private[this] var currentNode = trieRoot

    override def process(value: Byte): Boolean = {
      currentNode = currentNode.children(toUnsignedInt(value))
      currentNode.matchFor == -1
    }

    override def reset(): Unit = {
      currentPosition = 0
    }

    override def needleLength: Int = {
      val foundNeedleId = getFoundNeedleId
      if (foundNeedleId >= 0) needleLengths(foundNeedleId) else 0
    }

    override def getFoundNeedleId: Int = currentNode.matchFor

  }

  final class Context(trieRoot: TrieNode, needleLengths: Array[Int]) extends MultiSearchContext {

      override def newProcessor: Processor = new Processor(trieRoot, needleLengths)

  }

  private val AlphabetSize = 256

  private class TrieNode {
    val children: Array[TrieNode] = Array.ofDim(AlphabetSize)
    var matchFor: Int = -1
    def hasChildFor(ch: Int): Boolean = children(ch) != null
  }

  @varargs
  override def apply(needles: Array[Byte]*): Context = {

    val trieRoot = buildTrie(needles)
    linkSuffixes(trieRoot)

    new Context(trieRoot, needles.toArray.map(_.length))
  }

  override def apply(needle: Array[Byte]): Context = apply(Seq(needle): _*)

  private def buildTrie(needles: Seq[Array[Byte]]): TrieNode = {

    val trieRoot = new TrieNode

    for (stringId <- needles.indices) {
      val str = needles(stringId)
      var currentNode = trieRoot
      for (ch0 <- str) {
        val ch = toUnsignedInt(ch0)
        if (!currentNode.hasChildFor(ch)) {
          currentNode.children(ch) = new TrieNode
        }
        currentNode = currentNode.children(ch)
      }
      currentNode.matchFor = stringId
    }
    trieRoot
  }

  private def linkSuffixes(trieRoot: TrieNode): Unit = {

    val queue = mutable.Queue(trieRoot)
    val suffixLinks = mutable.AnyRefMap.empty[TrieNode, TrieNode]

    while (queue.nonEmpty) {
      val v = queue.dequeue()
      val u = suffixLinks.getOrElse(v, v)
      if (v.matchFor == -1) v.matchFor = u.matchFor
      for (ch <- 0 until AlphabetSize) {
        if (v.hasChildFor(ch)) {
          suffixLinks(v.children(ch)) = if (suffixLinks.contains(v) && u.hasChildFor(ch)) u.children(ch) else trieRoot
          queue += v.children(ch)
        } else {
          v.children(ch) = if (u.hasChildFor(ch)) u.children(ch) else trieRoot
        }
      }
    }
  }

}
