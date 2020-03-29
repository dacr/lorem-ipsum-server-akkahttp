package loremipsum.server.akkahttp.routing

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.CacheDirectives.{`max-age`, `must-revalidate`, `no-cache`, `no-store`}
import akka.http.scaladsl.model.headers.`Cache-Control`
import org.json4s.{DefaultFormats, Formats, native}
import org.json4s.native.Serialization
import org.json4s.ext.{JavaTimeSerializers, JavaTypesSerializers}

trait Routing {
  def routes: Route

  implicit val chosenSerialization: Serialization.type = native.Serialization
  implicit val chosenFormats: Formats = DefaultFormats.lossless ++ JavaTimeSerializers.all ++ JavaTypesSerializers.all
  val noClientCacheHeaders: List[HttpHeader] = List(`Cache-Control`(`no-cache`, `no-store`, `max-age`(0), `must-revalidate`))
  val clientCacheHeaders: List[HttpHeader] = List(`Cache-Control`(`max-age`(86400)))
}
