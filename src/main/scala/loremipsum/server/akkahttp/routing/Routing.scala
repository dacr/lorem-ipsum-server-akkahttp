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

import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.HttpHeader
import org.apache.pekko.http.scaladsl.model.headers.CacheDirectives.{`max-age`, `must-revalidate`, `no-cache`, `no-store`, `proxy-revalidate`}
import org.apache.pekko.http.scaladsl.model.headers.`Cache-Control`
import loremipsum.server.akkahttp.tools.JsonImplicits

trait Routing extends JsonImplicits {
  def routes: Route

  val noClientCacheHeaders: List[HttpHeader] = List(`Cache-Control`(`no-store`))

  val clientCacheHeaders: List[HttpHeader] = List(`Cache-Control`(`max-age`(3600), `must-revalidate`, `proxy-revalidate`))
}
