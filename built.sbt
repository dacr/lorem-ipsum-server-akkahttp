name         := "lorem-ipsum-server-akkahttp"
organization := "fr.janalyse"
description  := "lorem-ipsum service with API"

licenses += "NON-AI-APACHE2" -> url(s"https://github.com/non-ai-licenses/non-ai-licenses/blob/main/NON-AI-APACHE2")

scalaVersion := "3.10.1"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-release", "25")

lazy val versions = new {
  // client side dependencies
  val bootstrap = "5.3.8"
  val jquery    = "3.8.0"
  val awesome   = "6.7.2"

  // server side dependencies
  val pureConfig      = "0.17.11"
  val pekko           = "1.7.1"
  val pekkoHttp       = "1.4.2"
  val tapir           = "1.14.1"
  val jsoniter        = "2.43.0"
  val logback         = "1.6.7"
  val slf4j           = "2.0.20"
  val scalatest       = "3.2.20"
  val webjarsLocator  = "0.52"
  val loremIpsum      = "1.0.7"
}

// client side dependencies
libraryDependencies ++= Seq(
  "org.webjars" % "bootstrap"    % versions.bootstrap,
  "org.webjars" % "jquery"       % versions.jquery,
  "org.webjars" % "font-awesome" % versions.awesome
)

// server side dependencies
libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig-core"      % versions.pureConfig,
  "org.apache.pekko"      %% "pekko-http"           % versions.pekkoHttp,
  "org.apache.pekko"      %% "pekko-stream"         % versions.pekko,
  "org.apache.pekko"      %% "pekko-slf4j"          % versions.pekko,
  "org.apache.pekko"      %% "pekko-testkit"        % versions.pekko     % Test,
  "org.apache.pekko"      %% "pekko-stream-testkit" % versions.pekko     % Test,
  "org.apache.pekko"      %% "pekko-http-testkit"   % versions.pekkoHttp % Test,
  "com.softwaremill.sttp.tapir" %% "tapir-pekko-http-server" % versions.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-jsoniter-scala"    % versions.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % versions.tapir,
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % versions.jsoniter,
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % versions.jsoniter % "compile-internal",
  "org.slf4j"              % "slf4j-api"            % versions.slf4j,
  "ch.qos.logback"         % "logback-classic"      % versions.logback,
  "org.webjars"            % "webjars-locator"      % versions.webjarsLocator,
  "org.scalatest"         %% "scalatest"            % versions.scalatest % Test,
  "fr.janalyse"           %% "lorem-ipsum"          % versions.loremIpsum
)

Compile / mainClass    := Some("loremipsum.server.akkahttp.Main")
packageBin / mainClass := Some("loremipsum.server.akkahttp.Main")

enablePlugins(JavaServerAppPackaging)
enablePlugins(SbtTwirl)

homepage   := Some(url("https://github.com/dacr/lorem-ipsum-server-akkahttp"))
scmInfo    := Some(ScmInfo(url(s"https://github.com/dacr/lorem-ipsum-server-akkahttp.git"), s"git@github.com:dacr/lorem-ipsum-server-akkahttp.git"))
developers := List(
  Developer(
    id = "dacr",
    name = "David Crosson",
    email = "crosson.david@gmail.com",
    url = url("https://github.com/dacr")
  )
)

Universal / topLevelDirectory := None
Universal / packageName       := s"${name.value}"
