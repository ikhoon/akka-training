package http

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}

import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}

/**
  * Created by ikhoon on 2016. 7. 10..
  */
class HttpClientActor extends Actor with ActorLogging {

  final implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  import akka.pattern.pipe
  import context.dispatcher

  var send : ActorRef = _
  val http = Http(context.system)
  override def receive: Receive = {
    case "fetch" =>
      http.singleRequest(HttpRequest(uri = "http://akka.io")).pipeTo(self)
      send = sender()
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      val body = Unmarshal(entity).to[String]
      body.foreach(x => send ! "Got body " + x)
  }

}
