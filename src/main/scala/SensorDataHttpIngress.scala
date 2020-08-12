package sensordata

import akka.http.scaladsl.{Http, Http2}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.scaladsl.Flow
import cloudflow.akkastream._
import cloudflow.akkastream.util.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._

import scala.util.Failure

class SensorDataHttpIngress extends AkkaServerStreamlet {
  def shape                = StreamletShape.empty
  override def createLogic = new HttpServerLogic(this) {
    override def route(): Route = complete(StatusCodes.ImATeapot)

    // TODO update cloudstate to Akka HTTP 10.2.0 and use newServerAt:
    override def run(): Unit = {
      Http()
        .bindAndHandleAsync(
          Route.asyncHandler(route()),
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
