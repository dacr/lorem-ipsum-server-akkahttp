/*
 * Copyright 2021 David Crosson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package loremipsum.server.akkahttp.routing

import akka.http.scaladsl.model.HttpCharsets._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import loremipsum.server.akkahttp.ServiceDependencies
import loremipsum.server.akkahttp.templating.Templating
import loremipsum.{LoremIpsum, Paragraph}


case class HomeContext(
  context:PageContext,
  paragraphs: List[String]
)

case class ContentRouting(dependencies: ServiceDependencies) extends Routing {
  val templating = dependencies.templating
  val config = dependencies.config.loremIpsum.content
  val configSite = dependencies.config.loremIpsum.site
  val pageContext = PageContext(dependencies.config.loremIpsum)

  override def routes: Route = content

  def content: Route = {
    pathEndOrSingleSlash {
      get {
        complete {
          val minWordCount = config.minWordCount
          val maxWordCount = config.maxWordCount
          val wordCount = minWordCount + (Math.random() * (maxWordCount - minWordCount)).toInt
          val paragraphs: Seq[Paragraph] =
            LoremIpsum.generate(
              wordCount = wordCount,
              alwaysStartWithLorem = config.startWithLoremIpsum,
              truncate = config.truncate,
              randomize = config.randomize,
              sentencesBased = config.sentencesBased
            )
          val homeContext = HomeContext(
            context = pageContext,
            paragraphs = paragraphs.map(_.text()).toList
          )
          val templateContext = {
            import yamusca.imports._
            import yamusca.implicits._
            implicit val pageContextConverter = ValueConverter.deriveConverter[PageContext]
            implicit val homeContextConverter = ValueConverter.deriveConverter[HomeContext]
            homeContext.asContext
          }
          val templating: Templating = Templating(dependencies.config)
          val content = templating.layout("loremipsum/templates/home.mustache", templateContext)
          val contentType = `text/html` withCharset `UTF-8`
          HttpResponse(entity = HttpEntity(contentType, content), headers = noClientCacheHeaders)
        }
      }
    }
  }
}
