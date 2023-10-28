name         := "lorem-ipsum-server-akkahttp"
organization := "fr.janalyse"
homepage     := Some(new URL("https://github.com/dacr/lorem-ipsum-server-akkahttp"))

licenses += "NON-AI-APACHE2" -> url(s"https://github.com/non-ai-licenses/non-ai-licenses/blob/main/NON-AI-APACHE2")

scmInfo := Some(ScmInfo(url(s"https://github.com/dacr/lorem-ipsum-server-akkahttp.git"), s"git@github.com:dacr/lorem-ipsum-server-akkahttp.git"))

Compile / mainClass    := Some("loremipsum.server.akkahttp.Main")
packageBin / mainClass := Some("loremipsum.server.akkahttp.Main")

versionScheme := Some("semver-spec")

scalaVersion := "2.13.12"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

lazy val versions = new {
  // client side dependencies
  val swaggerui = "4.19.1"
  val bootstrap = "5.3.2"
  val jquery    = "3.7.1"

  // server side dependencies
  val pureConfig      = "0.17.4"
  val pekko           = "1.0.1"
  val pekkoHttp       = "1.0.0"
  val pekkoHttpJson4s = "2.1.1"
  val json4s          = "4.0.6"
  val logback         = "1.4.11"
  val slf4j           = "2.0.9"
  val scalatest       = "3.2.17"
  val webjarsLocator  = "0.48"
  val loremIpsum      = "1.0.6"
}

// client side dependencies
libraryDependencies ++= Seq(
  "org.webjars" % "swagger-ui" % versions.swaggerui,
  "org.webjars" % "bootstrap"  % versions.bootstrap,
  "org.webjars" % "jquery"     % versions.jquery
)

// server side dependencies
libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig"           % versions.pureConfig,
  "org.json4s"            %% "json4s-jackson"       % versions.json4s,
  "org.json4s"            %% "json4s-ext"           % versions.json4s,
  "org.apache.pekko"      %% "pekko-http"           % versions.pekkoHttp,
  "org.apache.pekko"      %% "pekko-stream"         % versions.pekko,
  "org.apache.pekko"      %% "pekko-slf4j"          % versions.pekko,
  "org.apache.pekko"      %% "pekko-testkit"        % versions.pekko     % Test,
  "org.apache.pekko"      %% "pekko-stream-testkit" % versions.pekko     % Test,
  "org.apache.pekko"      %% "pekko-http-testkit"   % versions.pekkoHttp % Test,
  "com.github.pjfanning"  %% "pekko-http-json4s"    % versions.pekkoHttpJson4s,
  "org.slf4j"              % "slf4j-api"            % versions.slf4j,
  "ch.qos.logback"         % "logback-classic"      % versions.logback,
  "org.webjars"            % "webjars-locator"      % versions.webjarsLocator,
  "org.scalatest"         %% "scalatest"            % versions.scalatest % Test,
  "fr.janalyse"           %% "lorem-ipsum"          % versions.loremIpsum
)

enablePlugins(SbtTwirl)

// TODO - to remove when twirl will be available for scala3
libraryDependencies := libraryDependencies.value.map {
  case module if module.name == "twirl-api" => module.cross(CrossVersion.for3Use2_13)
  case module                               => module
}
