package sensordata

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import cloudflow.akkastream._
import cloudflow.akkastream.util.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._

class SensorDataHttpIngress extends AkkaServerStreamlet {
  def shape                = StreamletShape.empty
  override def createLogic = new HttpServerLogic(this) {
    override def route(): Route = complete(StatusCodes.ImATeapot)
  }
}
