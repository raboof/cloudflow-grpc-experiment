package sensordata

import akka.stream.scaladsl.RunnableGraph
import cloudflow.akkastream.AkkaStreamlet
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.proto.ProtoInlet
import example.myapp.helloworld.grpc.HelloRequest

class Logger extends AkkaStreamlet {
  val inlet = ProtoInlet[HelloRequest]("in")
  val shape = StreamletShape.withInlets(inlet)

  override def createLogic = new RunnableGraphStreamletLogic() {
    override def runnableGraph(): RunnableGraph[_] =
      sourceWithCommittableContext(inlet)
        .map { hello => println(s"Saw ${hello.name}") }
        .to(committableSink)
  }
}
