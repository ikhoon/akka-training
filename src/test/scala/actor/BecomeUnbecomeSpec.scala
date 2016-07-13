package actor

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import org.scalatest.WordSpec

/**
  * Created by ikhoon on 2016. 7. 13..
  */
class BecomeUnbecomeSpec extends WordSpec {

  "become and unbecome" should {
    "become foo and bar" in {
      val system = ActorSystem()
      val actor: ActorRef = system.actorOf(Props[HotSwapActor])
      actor ! "bar"
      actor ! "foo"
      actor ! "bar"
      actor ! "foo"
      actor ! "bar"
      actor ! "foo"
      actor ! "bar"
      actor ! "foo"
    }

    "swap behavior" in {
      val system = ActorSystem()
      val actor: ActorRef = system.actorOf(Props[SwapActor], "swap")
      actor ! Swap
      actor ! Swap
      actor ! Swap
      actor ! Swap
      actor ! Swap
      actor ! Swap
      actor ! Swap
      actor ! Swap
      actor ! Swap
      actor ! Swap

    }

  }
}
