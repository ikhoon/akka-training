package actor

import akka.actor.{ActorRef, ActorSystem, Kill, PoisonPill, Props}
import akka.util.Timeout
import org.scalatest.WordSpec

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by ikhoon on 2016. 7. 12..
  */
class WatchActorSpec extends WordSpec {

  import akka.pattern.ask
  "watch actor" should {
    "kill child" in {
      val system = ActorSystem()
      val actor: ActorRef = system.actorOf(Props[WatchActor], "watch")
      implicit val timeout = Timeout(5 seconds)
      val res: Future[Any] = actor ? "kill"
      println(Await.result(res, Duration.Inf))
    }

    "kill actor " in {
      val system = ActorSystem()
      val actor: ActorRef = system.actorOf(Props[WatchActor], "watch")
      implicit val timeout = Timeout(5 seconds)
      actor ! "restart"
      val res: Future[Any] = actor ? "kill"
      println(Await.result(res, Duration.Inf))
    }
  }
}
