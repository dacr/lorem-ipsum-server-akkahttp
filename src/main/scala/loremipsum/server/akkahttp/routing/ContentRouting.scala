package loremipsum.server.akkahttp.routing

import akka.http.scaladsl.model.HttpCharsets._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import loremipsum.server.akkahttp.ServiceDependencies
import loremipsum.LoremIpsum


case class ContentRouting(dependencies: ServiceDependencies) extends Routing {
  val templating = dependencies.templating
  val config = dependencies.config

  override def routes: Route = content

  def content: Route = {
    pathSingleSlash {
      get {
        complete {
          val minWordCount = config.content.minWordCount
          val maxWordCount = config.content.maxWordCount
          val wordCount = minWordCount + (Math.random()*(maxWordCount - minWordCount)).toInt
          val paragraphs =
            LoremIpsum.generate(
              wordCount = wordCount,
              alwaysStartWithLorem = config.content.startWithLoremIpsum,
              truncate = config.content.truncate,
              randomize = config.content.randomize,
              sentencesBased = config.content.sentencesBased
            )
          val attributes = Map(
            "base" -> config.site.prefix.getOrElse(""),
            "url" -> config.site.url,
            "title" -> config.content.title,
            "paragraphs" -> paragraphs
          )
          val content = templating.layout("templates/home.mustache", attributes)
          val contentType = `text/html` withCharset `UTF-8`
          HttpResponse(entity = HttpEntity(contentType, content), headers = noClientCacheHeaders)
        }
      }
    }
  }
}
