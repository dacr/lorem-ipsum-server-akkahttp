package loremipsum.server.akkahttp.dependencies.loremipsumgenerator

import loremipsum.{LoremIpsum, Paragraph}
import loremipsum.server.akkahttp.ServiceConfig
import scala.math._

case class DefaultLoremIpsumGenerator(serviceConfig: ServiceConfig) extends LoremIpsumGenerator {
  val config = serviceConfig.loremIpsum.generator

  override def randomParagraphs(givenMinWordCount: Option[Int], givenMaxWordCount: Option[Int]): Seq[String] = {
    val minWordCount = givenMinWordCount.getOrElse(config.minWordCount)
    val maxWordCount = min(givenMaxWordCount.getOrElse(config.maxWordCount), config.maxWordCount)
    val wordCount = minWordCount + (Math.random() * (maxWordCount - minWordCount)).toInt
    if (wordCount <= 0) Seq.empty else {
      val paragraphs =
        LoremIpsum.generate(
        wordCount = wordCount,
        alwaysStartWithLorem = config.startWithLoremIpsum,
        truncate = config.truncate,
        randomize = config.randomize,
        sentencesBased = config.sentencesBased
      )
      paragraphs.map(_.text())
    }
  }

  override def randomText(givenMinWordCount: Option[Int], givenMaxWordCount: Option[Int]): String = {
    randomParagraphs(givenMinWordCount, givenMaxWordCount).mkString("\n")
  }
}
