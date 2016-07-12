package actor

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by ikhoon on 2016. 7. 13..
  */
class StopActor extends Actor with ActorLogging {
  val child = context.actorOf(Props[StopChildActor])

  override def receive: Receive = {
    case "interrupt-child" => context stop child
    case "done" => context stop self
  }

  override def postStop(): Unit = {
    log.info("stop parent")
  }
}

class StopChildActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case x => log.info(x.toString)
  }

  override def postStop(): Unit = {
    log.info("stop child")
  }
}
