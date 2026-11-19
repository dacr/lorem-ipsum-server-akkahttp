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
import loremipsum.server.akkahttp.api.{ApiEndpoints, ServiceInfo}
import loremipsum.server.akkahttp.tools.DateTimeTools

import java.util.UUID
import scala.concurrent.Future

case class LoremIpsumRouting(dependencies: ServiceDependencies) extends Routing with DateTimeTools {
  private val meta         = dependencies.config.loremIpsum.metaInfo
  private val startedDate  = now()
  private val instanceUUID = UUID.randomUUID()

  private val serviceInfo = ServiceInfo(
    instanceUUID = instanceUUID,
    startedOn = epochToUTCDateTime(startedDate),
    version = meta.version,
    buildDate = meta.dateTime
  )

  private val info = ApiEndpoints.info.serverLogicSuccess[Future](_ => Future.successful(serviceInfo))

  private val paragraphs = ApiEndpoints.paragraphs.serverLogicSuccess[Future] { (minWordCount, maxWordCount) =>
    Future.successful(dependencies.lorem.randomParagraphs(minWordCount, maxWordCount).toList)
  }

  private val text = ApiEndpoints.text.serverLogicSuccess[Future] { (minWordCount, maxWordCount) =>
    Future.successful(dependencies.lorem.randomText(minWordCount, maxWordCount))
  }

  override def endpoints = List(info, paragraphs, text)
}
