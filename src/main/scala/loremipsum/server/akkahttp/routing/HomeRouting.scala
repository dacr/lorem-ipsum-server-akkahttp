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
import yamusca.imports._
import yamusca.implicits._


case class HomeContext(
  context:PageContext,
  paragraphs: List[String]
)

case class HomeRouting(dependencies: ServiceDependencies) extends Routing {
  val contentConfig = dependencies.config.loremIpsum.content
  val configSite = dependencies.config.loremIpsum.site
  val pageContext = PageContext(dependencies.config.loremIpsum)
  val minWordCount = Some(contentConfig.minWordCount)
  val maxWordCount = Some(contentConfig.maxWordCount)

  override def routes: Route = content

  val templating: Templating = Templating(dependencies.config)
  implicit val pageContextConverter = ValueConverter.deriveConverter[PageContext]
  implicit val homeContextConverter = ValueConverter.deriveConverter[HomeContext]
  val homeLayout = (context: Context) => templating.makeTemplateLayout("loremipsum/templates/home.html")(context)

  def content: Route = {
    pathEndOrSingleSlash {
      get {
        complete {
          val paragraphs = dependencies.lorem.randomParagraphs(minWordCount, maxWordCount)
          val homeContext = HomeContext(
            context = pageContext,
            paragraphs = paragraphs.toList
          )
          val content = homeLayout(homeContext.asContext)
          val contentType = `text/html` withCharset `UTF-8`
          HttpResponse(entity = HttpEntity(contentType, content), headers = noClientCacheHeaders)
        }
      }
    }
  }
}
