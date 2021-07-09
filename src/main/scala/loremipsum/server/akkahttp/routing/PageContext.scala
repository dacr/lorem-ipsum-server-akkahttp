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
  contactEmail: String
)

object PageContext {
  def apply(config: LoremIpsumConfig) = {
    val site = config.site
    new PageContext(
      title = config.application.name,
      appcode = config.application.code,
      base = site.absolutePrefix,
      url = site.cleanedURL,
      baseURL = site.baseURL,
      apiURL = site.apiURL,
      swaggerURL = site.swaggerURL,
      swaggerUIURL = site.swaggerUserInterfaceURL,
      projectURL = config.metaInfo.projectURL,
      buildVersion = config.metaInfo.version,
      buildDateTime = config.metaInfo.dateTime,
      foregroundColor = config.content.foregroundColor,
      backgroundColor = config.content.backgroundColor,
      contactEmail = config.metaInfo.contact
    )
  }
}
