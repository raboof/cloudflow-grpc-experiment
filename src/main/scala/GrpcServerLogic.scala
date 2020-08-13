package sensordata

import akka.grpc.scaladsl.ServiceHandler
import akka.http.scaladsl.Http

import scala.collection.immutable
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives
import cloudflow.akkastream.{AkkaStreamletContext, Server}
import cloudflow.akkastream.util.scaladsl.HttpServerLogic
import cloudflow.streamlets.Dun

import scala.concurrent.Future
import scala.util.Failure

abstract class GrpcServerLogic(server: Server)(implicit context: AkkaStreamletContext) extends HttpServerLogic(server) {
  def handlers(): immutable.Seq[PartialFunction[HttpRequest, Future[HttpResponse]]]

  override def route(): Route = RouteDirectives.handle(ServiceHandler.concatOrNotFound(handlers(): _*))

  // TODO this could be the new implementation in HttpServerLogic
  override def run(): Unit = {
    Http()
      .newServerAt("0.0.0.0", containerPort)
      .bind(route())
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
