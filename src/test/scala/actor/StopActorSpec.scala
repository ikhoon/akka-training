package actor

import akka.actor.{ActorSystem, Props}
import org.scalatest.WordSpec

/**
  * Created by ikhoon on 2016. 7. 13..
  */
class StopActorSpec extends WordSpec {

  "stop actor" should {
    "child actor is killed by parent" in {
      val system = ActorSystem()
      val stopActor = system.actorOf(Props[StopActor])
      stopActor ! "interrupt-child"
      stopActor ! "done"

    }
  }

}
