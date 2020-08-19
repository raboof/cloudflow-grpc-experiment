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
}
