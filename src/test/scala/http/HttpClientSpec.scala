package http

import akka.{Done, NotUsed}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{HostConnectionPool, OutgoingConnection}
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.util.{ByteString, Timeout}
import org.scalatest.WordSpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
  * Created by ikhoon on 2016. 7. 8..
  */
class HttpClientSpec extends WordSpec {


  "connection level client side api" should  {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import system.dispatcher
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

  "host level client side api" should {


    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import system.dispatcher
    val pollClientFlow: Flow[(HttpRequest, Int), (Try[HttpResponse], Int), HostConnectionPool] = Http().cachedHostConnectionPool[Int]("akka.io")

    "http response body" in {
      // this code don't fetch all data!
      val httpResponseFuture: Future[(Try[HttpResponse], Int)] = Source.single(HttpRequest(uri = "/") -> 42)
        .via(pollClientFlow)
        .runWith(Sink.head)
      val response: (Try[HttpResponse], Int) = Await.result(httpResponseFuture, Duration.Inf)
      val responseBodyFuture: Future[ByteString] = response._1.get.entity.dataBytes.runWith(Sink.head)
      val byteString: ByteString = Await.result(responseBodyFuture, Duration.Inf)
      println(byteString.decodeString("UTF-8"))

    }

    "http response body as string using toStrict " in {

      val bodyFuture = Source.single(HttpRequest(uri = "/") -> 43)
        .via(pollClientFlow)
        .map{ case r => r._1.get.entity.toStrict(2.second).map { _.data }.map(_.utf8String) }
        .runWith(Sink.head)

      val result: String = Await.result(bodyFuture.flatMap(x => x), Duration.Inf)
      println(result)
    }

  }

  "request level client side api" should {
    "simple version" in {
      implicit val system = ActorSystem()
      implicit val materializer = ActorMaterializer()
      import system.dispatcher
      val requestFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))

      val result: HttpResponse = Await.result(requestFuture, Duration.Inf)
      val bodyFuture : Future[String] = Unmarshal(result.entity).to[String]
      val body: String = Await.result(bodyFuture, Duration.Inf)
      println(body)
    }

    "with in actor" in {
      import akka.pattern.ask
      implicit val timout = Timeout(5 seconds)

      val system = ActorSystem("actorsytem")
      val actor: ActorRef = system.actorOf(Props[HttpClientActor], "akka")
      val r = actor ? "fetch"
      println(Await.result(r, Duration.Inf))

    }

  }

  "client side websocket api" should {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import system.dispatcher
    "single websocket request" in {

      // sink consume message
      val printSink: Sink[Message, Future[Done]] = Sink.foreach {
        case message: TextMessage.Strict =>
          println(s"## response : ${message.text}")
      }

      // send message
      val helloSource = Source(List(TextMessage("1"), TextMessage("2"), TextMessage("3"), TextMessage("4"), TextMessage("5")))

      // how to connect it
      val flow = Flow.fromSinkAndSourceMat(printSink, helloSource)(Keep.left)

      // request
      val (upgradeResponse: Future[WebSocketUpgradeResponse], closed: Future[Done]) = Http().singleWebSocketRequest(WebSocketRequest("ws://echo.websocket.org"), flow)

      val connection = upgradeResponse.map { upgrade =>
        if(upgrade.response.status == StatusCodes.SwitchingProtocols) {
          Done
        }
        else {
          throw new RuntimeException(s"Connection failed ${upgrade.response.status}")
        }
      }
      connection.onComplete(println)

      closed.foreach(_ => println("closed"))

      Await.result(closed, Duration.Inf)

    }

    "connection flow " in {
      val incoming = Sink.foreach[Message] {
        case message : TextMessage.Strict => println(s"incoming : ${message.text}")
      }

      val outgoing = Source.single(TextMessage("hello world!"))

      val webSocketFlow = Http().webSocketClientFlow(WebSocketRequest("ws://echo.websocket.org"))
      val (upgradeResponse, closed) = outgoing
        .viaMat(webSocketFlow)(Keep.right)
        .toMat(incoming)(Keep.both)
        .run()

      upgradeResponse.map { upgrade =>
        if(upgrade.response.status == StatusCodes.SwitchingProtocols) {
          Done
        }
        else {
          throw new RuntimeException(s"Connection failed ${upgrade.response.status}")
        }
      }

      closed.foreach(_ => println("closed"))
      Await.result(closed, Duration.Inf)
    }

  }
}


