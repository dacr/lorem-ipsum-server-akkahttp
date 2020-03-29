package loremipsum.server.akkahttp.routing

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.ContentTypeResolver.Default
import loremipsum.server.akkahttp.ServiceDependencies
import org.webjars.WebJarAssetLocator

object AssetsRouting {
  protected val assetLocator = new WebJarAssetLocator()
}

case class AssetsRouting(dependencies:ServiceDependencies) extends Routing {

  private def staticRoutes = {
    val staticResourcesSubDirectories = List("js", "css", "images", "fonts", "pdf", "txt")
    val routes = for {resourceDirectory <- staticResourcesSubDirectories} yield {
      path(resourceDirectory / RemainingPath) { resource =>
        respondWithHeaders(noClientCacheHeaders) {
          getFromResource(s"static-content/$resourceDirectory/${resource.toString()}")
        }
      }
    }
    routes.reduce(_ ~ _)
  }

  private def assetsRoutes:Route =
    rejectEmptyResponse  {
      path("assets" / Segment / RemainingPath ) { (webjar, path) =>
        respondWithHeaders(clientCacheHeaders) {
          val resourcePath = AssetsRouting.assetLocator.getFullPath(webjar, path.toString())
          getFromResource(resourcePath)
        }
      }
    }

  override def routes:Route = assetsRoutes ~ staticRoutes
}
