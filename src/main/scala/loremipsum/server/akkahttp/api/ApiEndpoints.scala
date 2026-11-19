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
package loremipsum.server.akkahttp.api

import loremipsum.server.akkahttp.api.ContentCodecs.given
import sttp.tapir.*
import sttp.tapir.json.jsoniter.*

/** The API definition, used both to serve the API and to generate its OpenAPI documentation */
object ApiEndpoints {

  val description: String =
    """Lorem ipsum random text generator.
      |
      |The size of the generated content can be tuned with the optional `minWordCount` and `maxWordCount` query parameters,
      |the service configuration defines their default values as well as the upper bound of `maxWordCount`.""".stripMargin

  object Tags {
    val loremIpsum = "lorem-ipsum"
    val service    = "service"

    val descriptions: List[(String, String)] = List(
      loremIpsum -> "Lorem ipsum content generation",
      service    -> "Service information and health"
    )
  }

  private val minWordCount =
    query[Option[Int]]("minWordCount").description("Minimum number of words in the generated lorem ipsum content")

  private val maxWordCount =
    query[Option[Int]]("maxWordCount").description("Maximum number of words in the generated lorem ipsum content")

  private val serviceEndpoint    = endpoint.tag(Tags.service)
  private val loremIpsumEndpoint = endpoint.tag(Tags.loremIpsum).in("api")

  // -------------------------------------------------------------------------------------------------------------------
  // service

  val health: PublicEndpoint[Unit, Unit, Health, Any] =
    serviceEndpoint
      .name("health")
      .summary("Service health check")
      .get
      .in("health")
      .out(jsonBody[Health].description("service health status"))

  val info: PublicEndpoint[Unit, Unit, ServiceInfo, Any] =
    serviceEndpoint
      .name("info")
      .summary("General information about the service")
      .get
      .in("api" / "info")
      .out(jsonBody[ServiceInfo].description("service information"))

  // -------------------------------------------------------------------------------------------------------------------
  // lorem ipsum

  val paragraphs: PublicEndpoint[(Option[Int], Option[Int]), Unit, List[String], Any] =
    loremIpsumEndpoint
      .name("paragraphs")
      .summary("Get randomly generated lorem ipsum paragraphs")
      .get
      .in("paragraphs")
      .in(minWordCount)
      .in(maxWordCount)
      .out(jsonBody[List[String]].description("the generated paragraphs"))

  val text: PublicEndpoint[(Option[Int], Option[Int]), Unit, String, Any] =
    loremIpsumEndpoint
      .name("text")
      .summary("Get randomly generated lorem ipsum text, paragraphs are separated by new lines")
      .get
      .in("text")
      .in(minWordCount)
      .in(maxWordCount)
      .out(jsonBody[String].description("the generated text as a JSON string"))

  // -------------------------------------------------------------------------------------------------------------------

  val all: List[AnyEndpoint] = List(
    paragraphs,
    text,
    info,
    health
  )
}
