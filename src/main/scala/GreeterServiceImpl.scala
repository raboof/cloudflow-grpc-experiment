package sensordata

import akka.NotUsed
import akka.stream.scaladsl.Source
import cloudflow.akkastream.WritableSinkRef
import example.myapp.helloworld.grpc.{GreeterService, HelloReply, HelloRequest}

import scala.concurrent.{ExecutionContext, Future}

class GreeterServiceImpl(sink: WritableSinkRef[HelloRequest])(implicit ec: ExecutionContext) extends GreeterService {
  override def sayHello(in: HelloRequest): Future[HelloReply] = {
    println("howdy")
    sink.write(in).map(_ => { println("mapped"); HelloReply(s"Good to see you using CloudFlow, ${in.name}")})
  }

  override def itKeepsTalking(in: Source[HelloRequest, NotUsed]): Future[HelloReply] = ???
  override def itKeepsReplying(in: HelloRequest): Source[HelloReply, NotUsed] = ???
  override def streamHellos(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = ???
}
