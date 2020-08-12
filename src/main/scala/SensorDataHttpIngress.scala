package sensordata

import akka.NotUsed
import akka.grpc.scaladsl.ServerReflection
import akka.grpc.scaladsl.ServiceHandler
import akka.http.scaladsl.{Http, Http2}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.scaladsl.{Flow, Source}
import cloudflow.akkastream._
import cloudflow.akkastream.util.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._
import example.myapp.helloworld.grpc.{GreeterService, GreeterServiceHandler, HelloReply, HelloRequest}
import grpc.reflection.v1alpha.reflection.ServerReflectionHandler

import scala.concurrent.Future
import scala.util.Failure

class SensorDataHttpIngress extends AkkaServerStreamlet {
  def shape                = StreamletShape.empty

  val implementation = new GreeterService {
    override def sayHello(in: HelloRequest): Future[HelloReply] =
      Future.successful(HelloReply(s"Good to see you using CloudFlow, ${in.name}"))
    override def itKeepsTalking(in: Source[HelloRequest, NotUsed]): Future[HelloReply] = ???
    override def itKeepsReplying(in: HelloRequest): Source[HelloReply, NotUsed] = ???
    override def streamHellos(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = ???
  }

  override def createLogic = new HttpServerLogic(this) {
    override def route(): Route = complete(StatusCodes.ImATeapot)

    // TODO update cloudstate to Akka HTTP 10.2.0 and use newServerAt:
    override def run(): Unit = {
      Http()
        .bindAndHandleAsync(
          ServiceHandler.concatOrNotFound(
            GreeterServiceHandler.partial(implementation),
            ServerReflection.partial(List(GreeterService))
          ),
          //Route.asyncHandler(route()),
          "0.0.0.0",
          containerPort,
          Http().defaultServerHttpContext
        )
        .map { binding ⇒
          context.signalReady()
          system.log.info(s"Bound to ${binding.localAddress.getHostName}:${binding.localAddress.getPort}")
          // this only completes when StreamletRef executes cleanup.
          context.onStop { () ⇒
            system.log.info(s"Unbinding from ${binding.localAddress.getHostName}:${binding.localAddress.getPort}")
            binding.unbind().map(_ ⇒ Dun)
          }
          binding
        }
        .andThen {
          case Failure(cause) ⇒
            system.log.error(cause, s"Failed to bind to $containerPort.")
            context.stop()
        }
    }
  }
}
