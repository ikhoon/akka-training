package actor

import akka.actor.{ActorSystem, Props}
import org.scalatest.WordSpec

/**
  * Created by ikhoon on 2016. 7. 14..
  */
class StashSpec extends WordSpec {

  "stash message" should {
    "stash & unstashAll()" in {
      val system = ActorSystem()
      val actor = system.actorOf(Props[StashActor])
      actor ! "a"
      actor ! "b"
      actor ! "c"
      actor ! "write"
      actor ! "d"
      actor ! "write"
      actor ! "e"
      actor ! "open"
      actor ! "f"
      actor ! "g"
      actor ! "h"
      actor ! "i"
      actor ! "j"
      actor ! "k"
      actor ! "close"
      actor ! "m"
      actor ! "n"
      actor ! "write"
      actor ! "open"
      actor ! "write"
    }
  }

}
