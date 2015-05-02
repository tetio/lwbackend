name := """LWBackend"""

organization  := "com.buzzfactory.lw"

version       := "1.0"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",

    "org.json4s" %% "json4s-native" % "3.2.10",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "org.scalatest" %% "scalatest" % "2.2.2" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",

    "org.json4s" %% "json4s-jackson" % "3.2.11",
    "com.typesafe.slick" %% "slick" % "2.1.0",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
  )
}

Revolver.settings
