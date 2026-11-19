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
package loremipsum.server.akkahttp

import loremipsum.server.akkahttp.routing.{AdminRouting, AssetsRouting, HomeRouting, LoremIpsumRouting, SwaggerRouting}
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter

import scala.concurrent.ExecutionContext

case class ServiceRoutes(dependencies: ServiceDependencies) {
  private given ExecutionContext = ExecutionContext.global

  val config = dependencies.config.loremIpsum

  val endpoints = List(
    SwaggerRouting(dependencies),
    LoremIpsumRouting(dependencies),
    AdminRouting(dependencies),
    HomeRouting(dependencies),
    AssetsRouting(dependencies)
  ).flatMap(_.endpoints)

  private val rawRoutes: Route = PekkoHttpServerInterpreter().toRoute(endpoints)

  val routes: Route =
    config.site.cleanedPrefix
      .map { p =>
        pathPrefix(p) {
          rawRoutes
        }
      }
      .getOrElse(rawRoutes)
}
