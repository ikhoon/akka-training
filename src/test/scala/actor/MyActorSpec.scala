package actor

import akka.actor.{ActorRef, ActorSystem, Props}
import org.scalatest.WordSpec

/**
  * Created by ikhoon on 2016. 7. 10..
  */
class MyActorSpec extends WordSpec {

  import akka.pattern._
  "simple actor" should {

    "send message" in {
      val system = ActorSystem()
      val actor = system.actorOf(Props[MyActor])
      actor ! "test"
    }

  }

  "edge case of create actor" should {
    "value class " in {
    }
  }

}
