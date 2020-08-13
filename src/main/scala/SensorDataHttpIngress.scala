package sensordata

import akka.grpc.scaladsl.ServerReflection
import cloudflow.akkastream._
import cloudflow.streamlets._
import cloudflow.streamlets.proto.ProtoOutlet
import example.myapp.helloworld.grpc.{GreeterService, GreeterServiceHandler, HelloRequest}

class SensorDataHttpIngress extends AkkaServerStreamlet {
  val out                  = ProtoOutlet[HelloRequest]("out", RoundRobinPartitioner)
  def shape                = StreamletShape.withOutlets(out)

  override def createLogic = new GrpcServerLogic(this) {
    override def handlers() = {
      List(
        GreeterServiceHandler.partial(new GreeterServiceImpl(sinkRef(out))),
        ServerReflection.partial(List(GreeterService)))
    }
  }
}
