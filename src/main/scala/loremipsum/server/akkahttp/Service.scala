package loremipsum.server.akkahttp

import akka.actor.{ActorSystem, Terminated}
import akka.http.scaladsl._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.slf4j.Logger

import scala.concurrent.{ExecutionContextExecutor, Future}

class Service(name: String, interface: String, port: Int, routes: Route) {
  val logger: Logger = org.slf4j.LoggerFactory.getLogger(name)
  logger.info(s"Service $name is starting")

  implicit val system: ActorSystem = akka.actor.ActorSystem(s"akka-http-$name-system")
  implicit val materializer: ActorMaterializer.type = akka.stream.ActorMaterializer
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(handler = routes, interface = interface, port = port)
  bindingFuture.map(_ => logger.info(s"Service $name is started"))

  def shutdown(): Future[Terminated] = {
    bindingFuture
      .flatMap(_.unbind())
      .flatMap { _ => system.terminate() }
  }
}

object Service {
  def apply(name: String, interface: String, port: Int, routing: Route): Service = {
    new Service(name = name, interface = interface, port = port, routes = routing)
  }
}
