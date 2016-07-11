package actor

import actor.RightMessageLocationActor.{GoodBye, Greeting}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by ikhoon on 2016. 7. 11..
  */
class RecommendPracticeSpec extends WordSpec with Matchers{

  "recommended actor create pattern" should {
    implicit val timeout = Timeout(5 seconds)
    val system = ActorSystem()
    "normal create" in {
      import akka.pattern.ask
      val actor: ActorRef = system.actorOf(Props[DoubleActor])
      val future: Future[Any] = actor ? 12
      Await.result(future, Duration.Inf) shouldBe 34

    }

    "message location" in {

      val actor = system.actorOf(Props[RightMessageLocationActor])
      actor ! Greeting("ikhoon")
      actor ! GoodBye
    }
  }

}
