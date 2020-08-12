import sbt._
import sbt.Keys._

lazy val experiment = (project in file("."))
  .enablePlugins(CloudflowApplicationPlugin, CloudflowAkkaPlugin)
  .settings(
    scalaVersion := "2.12.11",
    runLocalConfigFile := Some("src/main/resources/local.conf"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka"      %% "akka-http-spray-json"      % "10.1.12",
    )
  )
