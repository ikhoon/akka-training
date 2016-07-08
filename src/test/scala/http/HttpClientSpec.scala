package http

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{HostConnectionPool, OutgoingConnection}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import org.scalatest.WordSpec

import scala.concurrent.ExecutionContext.Implicits.global
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
      val requestFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))

      val result: HttpResponse = Await.result(requestFuture, Duration.Inf)
      val bodyFuture : Future[String] = Unmarshal(result.entity).to[String]
      val body: String = Await.result(bodyFuture, Duration.Inf)
      println(body)
    }

    "with in actor" in {

      val system = ActorSystem("actorsytem")
      val actor: ActorRef = system.actorOf(Props[HttpClientActor], "akka")
      actor ! "fetch"
      Thread.sleep(1000)

    }

  }
}

class HttpClientActor extends Actor with ActorLogging {


  import akka.pattern.pipe

  final implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))


  val http = Http(context.system)
  override def receive: Receive = {
    case "fetch" => http.singleRequest(HttpRequest(uri = "http://akka.io")).pipeTo(self)
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      val body = Unmarshal(entity).to[String]
      body.foreach(x => log.info("Got body " + x))
  }
}
