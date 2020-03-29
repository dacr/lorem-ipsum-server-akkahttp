package loremipsum.server.akkahttp.routing

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import loremipsum.server.akkahttp.ServiceDependencies


case class Health(alive:Boolean=true, description: String = "alive")

object AdminRouting {
  val alive = Health()
}
case class AdminRouting(dependencies: ServiceDependencies) extends Routing {
  private def ping: Route = path("health") { get { complete(AdminRouting.alive) } }

  override def routes: Route = ping
}
