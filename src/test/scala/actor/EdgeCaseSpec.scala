package actor

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import org.scalatest.WordSpec
import scala.concurrent.duration._

import scala.concurrent.Future

/**
  * Created by ikhoon on 2016. 7. 10..
  */
class EdgeCaseSpec extends WordSpec {

  "edge case of create actor " should {
    import akka.pattern.ask
    implicit val timeout = Timeout(5 seconds)
    val system = ActorSystem()
    import system.dispatcher
    "with value class" in {
      intercept[IllegalArgumentException] {
        val actor: ActorRef = system.actorOf(Props(classOf[ValueActor], MyValueClass(10)))
        val future: Future[Any] = actor ? 10L
        future.foreach(println)
      }
    }

    "with default value" in {
      intercept[IllegalArgumentException] {
        val actor = system.actorOf(Props(classOf[DefaultValueActor], 10))
      }

      intercept[IllegalArgumentException] {
        val actor2 = system.actorOf(Props(classOf[DefaultValueActor2]))
      }

      intercept[IllegalArgumentException] {
        val actor3 = system.actorOf(Props[DefaultValueActor2])
      }
    }


  }
}
