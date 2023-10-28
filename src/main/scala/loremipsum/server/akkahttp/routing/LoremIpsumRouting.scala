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

import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import com.github.pjfanning.pekkohttpjson4s.Json4sSupport._
import loremipsum.server.akkahttp.ServiceDependencies
import loremipsum.server.akkahttp.tools.DateTimeTools

import java.util.UUID

case class LoremIpsumRouting(dependencies: ServiceDependencies) extends Routing with DateTimeTools {
  val apiURL = dependencies.config.loremIpsum.site.apiURL
  val meta = dependencies.config.loremIpsum.metaInfo
  val startedDate = now()
  val instanceUUID = UUID.randomUUID().toString

  override def routes: Route = pathPrefix("api") {
    concat(info, loremIpsumParagraphs, loremIpsumText)
  }

  def info: Route = {
    path("info") {
      get {
        complete(
          Map(
            "instanceUUID" -> instanceUUID,
            "startedOn" -> epochToUTCDateTime(startedDate),
            "version" -> meta.version,
            "buildDate" -> meta.buildDateTime
          )
        )
      }
    }
  }

  def loremIpsumParagraphs: Route = {
    path("paragraphs") {
      get {
        parameters("minWordCount".as[Int].optional, "maxWordCount".as[Int].optional) { (minWordCount, maxWordCount) =>
          complete {
            dependencies.lorem.randomParagraphs(minWordCount, maxWordCount)
          }
        }
      }
    }
  }


  def loremIpsumText: Route = {
    path("text") {
      get {
        parameters("minWordCount".as[Int].optional, "maxWordCount".as[Int].optional) { (minWordCount, maxWordCount) =>
          complete {
            dependencies.lorem.randomText(minWordCount, maxWordCount)
          }
        }
      }
    }
  }


}
