/*
 * Copyright 2020-2022 David Crosson
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

import loremipsum.server.akkahttp.ServiceDependencies
import loremipsum.server.akkahttp.templates.html.HomeTemplate
import sttp.tapir.*

import scala.concurrent.Future

case class HomeContext(
  context: PageContext,
  paragraphs: List[String]
)

case class HomeRouting(dependencies: ServiceDependencies) extends Routing {
  private val contentConfig = dependencies.config.loremIpsum.content
  private val pageContext   = PageContext(dependencies.config.loremIpsum)
  private val minWordCount  = Some(contentConfig.minWordCount)
  private val maxWordCount  = Some(contentConfig.maxWordCount)

  private val home =
    endpoint.get
      .in("") // root path only, an endpoint without any path input matches all paths
      .out(htmlBodyUtf8)
      .out(header(Routing.noClientCacheHeader))
      .serverLogicSuccess[Future] { _ =>
        Future.successful {
          val paragraphs  = dependencies.lorem.randomParagraphs(minWordCount, maxWordCount)
          val homeContext = HomeContext(context = pageContext, paragraphs = paragraphs.toList)
          HomeTemplate.render(homeContext).toString
        }
      }

  override def endpoints = List(home)
}
