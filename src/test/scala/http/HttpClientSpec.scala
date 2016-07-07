package http

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import org.scalatest.{FunSuite, WordSpec}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by ikhoon on 2016. 7. 8..
  */
class HttpClientSpec extends WordSpec {


  "connection level client side api" should  {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val connectionFlow: Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]] = Http().outgoingConnection("akka.io")

    // reference : http://stackoverflow.com/questions/31532838/get-whole-httpresponse-body-as-a-string-with-akka-streams-http
    "http response body as string " in {
      val httpResponseFuture: Future[HttpResponse] = Source.single(HttpRequest(uri = "/"))
        .via(connectionFlow)
        .runWith(Sink.head)
      val response: HttpResponse = Await.result(httpResponseFuture, Duration.Inf)
      val responseBodyFuture: Future[ByteString] = response.entity.dataBytes.runWith(Sink.head)
      val byteString: ByteString = Await.result(responseBodyFuture, Duration.Inf)
      println(byteString.decodeString("UTF-8"))
    }

    "http response body as string using toStrict " in {

      val bodyFuture = Source.single(HttpRequest(uri = "/"))
        .via(connectionFlow)
        .map{ case r => r.entity.toStrict(2.second).map { _.data }.map(_.utf8String) }
          .runWith(Sink.head)

      val result: String = Await.result(bodyFuture.flatMap(x => x), Duration.Inf)
      println(result)
    }

    "http response body as string using unmarshal" in {

      val bodyFuture = Source.single(HttpRequest(uri = "/"))
        .via(connectionFlow)
        .map{ case r => Unmarshal(r.entity).to[String] }
        .runWith(Sink.head)

      val result: String = Await.result(bodyFuture.flatMap(x => x), Duration.Inf)
      println(result)
    }
  }
}
