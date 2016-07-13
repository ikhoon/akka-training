package actor

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}

/**
  * Created by ikhoon on 2016. 7. 13..
  */

class HotSwapActor extends Actor with ActorLogging {
  import context._
  def angry: Receive = {
    case "foo" =>
      log.info("angry foo to sender")
      sender() ! "I am already angry?"
    case "bar" =>
      log.info("angry bar to happy")
      become(happy)
  }

  def happy: Receive = {
    case "bar" =>
      log.info("happy bar to sender")
      sender() ! "I am already happy :-)"
    case "foo" =>
      log.info("happy foo to angry")
      become(angry)
  }

  def receive = {
    case "foo" =>
      log.info("receive foo to angry")
      become(angry)
    case "bar" =>
      log.info("receive bar to happy")
      become(happy)
  }
}

object Swap

class SwapActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case Swap => log.info("hi")
      context.become({
        case Swap => log.info("ih")
          context.unbecome()
      }, discardOld = false)
  }
}
