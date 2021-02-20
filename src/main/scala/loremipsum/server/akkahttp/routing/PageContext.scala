package loremipsum.server.akkahttp.routing

import loremipsum.server.akkahttp.LoremIpsumConfig


case class PageContext(
  title: String,
  appcode: String,
  base: String,
  url: String,
  baseURL: String,
  apiURL: String,
  swaggerURL: String,
  swaggerUIURL: String,
  projectURL: String,
  buildVersion: String,
  buildDateTime: String,
  backgroundColor: String,
  foregroundColor: String,
)

object PageContext {
  def apply(webEchoConfig: LoremIpsumConfig) = {
    val site = webEchoConfig.site
    new PageContext(
      title = webEchoConfig.application.name,
      appcode = webEchoConfig.application.code,
      base = site.absolutePrefix,
      url = site.cleanedURL,
      baseURL = site.baseURL,
      apiURL = site.apiURL,
      swaggerURL = site.swaggerURL,
      swaggerUIURL = site.swaggerUserInterfaceURL,
      projectURL =  webEchoConfig.metaInfo.projectURL,
      buildVersion = webEchoConfig.metaInfo.version,
      buildDateTime = webEchoConfig.metaInfo.dateTime,
      foregroundColor = webEchoConfig.content.foregroundColor,
      backgroundColor = webEchoConfig.content.backgroundColor,
    )
  }
}
