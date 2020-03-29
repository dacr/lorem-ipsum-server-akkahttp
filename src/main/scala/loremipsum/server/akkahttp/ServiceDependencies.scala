package loremipsum.server.akkahttp

import loremipsum.server.akkahttp.templating.{ScalateTemplating, Templating}

trait ServiceDependencies {
  val config:ServiceConfig
  val templating:Templating
}

object ServiceDependencies {
  def defaults:ServiceDependencies = new ServiceDependencies {
    override val config: ServiceConfig = ServiceConfig()
    override val templating: Templating = ScalateTemplating(config)
  }
}