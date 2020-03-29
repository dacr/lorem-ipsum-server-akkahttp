package loremipsum.server.akkahttp

import org.slf4j.LoggerFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration

case class ApplicationConfig(
  name:String,
  code:String,
)

case class SystemConfig(
  shutdownTimeout:FiniteDuration
)

case class HttpConfig(
  listeningInterface:String,
  listeningPort:Int,
)

case class SiteConfig(
  prefix:Option[String],
  url:String
) {
  val cleanedPrefix = prefix.map(_.trim).filter(_.size>0)
}

case class Content(
  title:String,
  startWithLoremIpsum:Boolean,
)

case class ServiceConfig(
  application:ApplicationConfig,
  system:SystemConfig,
  http:HttpConfig,
  site:SiteConfig,
  content:Content,
)

object ServiceConfig {
  def apply():ServiceConfig = {
    val logger = LoggerFactory.getLogger("ServiceConfig")
    ConfigSource.default.load[ServiceConfig] match {
      case Left(issues) =>
        issues.toList.foreach{issue => logger.error(issue.toString)}
        throw new RuntimeException("Invalid application configuration\n"+issues.toList.map(_.toString).mkString("\n"))
      case Right(config) =>
        config
    }
  }
}