package loremipsum.server.akkahttp.dependencies.loremipsumgenerator

import loremipsum.Paragraph

trait LoremIpsumGenerator {
  def randomParagraphs(givenMinWordCount:Option[Int], givenMaxWordCount:Option[Int]):Seq[String]
  def randomText(givenMinWordCount:Option[Int], givenMaxWordCount:Option[Int]):String
}
