package actor

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by ikhoon on 2016. 7. 11..
  */
class RecommendPracticeSpec extends WordSpec with Matchers{

  "recommended actor create pattern" should {
    "normal create" in {
      import akka.pattern.ask
      implicit val timeout = Timeout(5 seconds)
      val system = ActorSystem()
      val actor: ActorRef = system.actorOf(Props[DoubleActor])
      val future: Future[Any] = actor ? 12
      Await.result(future, Duration.Inf) shouldBe 34

    }
  }
}
