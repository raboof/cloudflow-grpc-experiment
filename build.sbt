import sbt._
import sbt.Keys._

enablePlugins(
  CloudflowApplicationPlugin,
  CloudflowAkkaPlugin,
  AkkaGrpcPlugin
)

//akkaGrpcGeneratedSources := List(AkkaGrpc.Server)

// CommonSettingsAndTasksPlugin and AkkaGrpcPlugin both contribute
// ScalaPB code generation to the PB.targets.
//
// For now, we explicitly remove the duplicate. If we add 'first-class'
// support for Akka gRPC in cloudflow perhaps we should detect it there
// and in that case don't add the generator there.
PB.targets in Compile ~= (f => {
  // Skip the cloudflow-provided Scala code generator, use the akka-grpc-generated one
  f.filterNot(t => t.generator.name == "scala" && t.outputPath.getName.contains("scalapb"))
})

scalaVersion := "2.12.12"
//runLocalConfigFile := Some("src/main/resources/local.conf")
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http2-support"      % "10.1.12",
)
