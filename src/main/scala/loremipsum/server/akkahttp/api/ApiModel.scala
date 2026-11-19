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

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import sttp.tapir.Schema
import sttp.tapir.Schema.annotations.description

import java.time.OffsetDateTime
import java.util.UUID

case class Health(
  @description("Always true when the service is able to respond")
  alive: Boolean = true,
  @description("Health status description")
  description: String = "alive"
) derives Schema

object Health {
  // default values must be rendered, otherwise the default health status would be encoded as an empty object
  given JsonValueCodec[Health] = JsonCodecMaker.make(CodecMakerConfig.withTransientDefault(false))
}

case class ServiceInfo(
  @description("This instance unique identifier, always updated on (re)start")
  instanceUUID: UUID,
  @description("Last date time when this instance has been (re)started")
  startedOn: OffsetDateTime,
  @description("Software version")
  version: String,
  @description("Software build date")
  buildDate: String
) derives Schema

object ServiceInfo {
  given JsonValueCodec[ServiceInfo] = JsonCodecMaker.make
}

/** Codecs for the generated lorem ipsum content, returned as JSON strings */
object ContentCodecs {
  given textCodec: JsonValueCodec[String]             = JsonCodecMaker.make
  given paragraphsCodec: JsonValueCodec[List[String]] = JsonCodecMaker.make
}
