package loremipsum.server.akkahttp.templating

import loremipsum.server.akkahttp.ServiceConfig
import org.fusesource.scalate._

trait Templating {
  def layout(templateName:String, properties:Map[String,Any]):String
}

case class ScalateTemplating(config:ServiceConfig) extends Templating {
  private val engine = new TemplateEngine

  override def layout(templateName:String, properties:Map[String,Any]):String = {
    engine.layout(templateName, properties)
  }
}
