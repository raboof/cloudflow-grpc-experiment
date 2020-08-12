import sbt._
import sbt.Keys._

enablePlugins(
  CloudflowApplicationPlugin,
  CloudflowAkkaPlugin,
  AkkaGrpcPlugin
)

//akkaGrpcGeneratedSources := List(AkkaGrpc.Server)

// CommonSettingsAndTasksPlugin and AkkaGrpcPlugin both contribute
// ScalaPB code generation to the PB.targets, but with a different
// target directory, causing compiling the second class to fail because
// the first already existed (even though they are identical).
// For now, we explicitly remove the duplicate
PB.targets in Compile ~= (f => {
  f.filter(!_.outputPath.getName.contains("scalapb"))
  })

scalaVersion := "2.12.11"
//runLocalConfigFile := Some("src/main/resources/local.conf")
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http2-support"      % "10.1.12",
)
