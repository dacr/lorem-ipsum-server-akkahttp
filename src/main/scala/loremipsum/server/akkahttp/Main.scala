package loremipsum.server.akkahttp

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import loremipsum.server.akkahttp.routing.{AdminRouting, AssetsRouting, ContentRouting}
import org.slf4j.Logger

class ServiceStarter(dependencies: ServiceDependencies) {
  import dependencies.config

  val routing: Route = List(
    AssetsRouting(dependencies),
    AdminRouting(dependencies),
    ContentRouting(dependencies),
  ).map(_.routes).reduce(_ ~ _)

  val prefixedRouting: Route = config.site.cleanedPrefix.map{p => pathPrefix(p) { routing }}.getOrElse(routing)

  val service = Service(
    name = config.application.code,
    interface = config.http.listeningInterface,
    port = config.http.listeningPort,
    routing = prefixedRouting
  )
}

object Main {
  val logger: Logger = org.slf4j.LoggerFactory.getLogger("Main")

  def main(args: Array[String]): Unit = {
    logger.info(s"Application starting")
    val dependencies = ServiceDependencies.defaults
    new ServiceStarter(dependencies)
  }
}
