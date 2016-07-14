package actor

import akka.actor.{Actor, ActorLogging, Stash}

/**
  * Created by ikhoon on 2016. 7. 14..
  */
class StashActor extends Actor with Stash with ActorLogging {

  override def receive: Receive = {
    case "open" =>
      log.info("open...")
      unstashAll()
      context.become({
        case "write" => log.info("writing...")
        case "close" =>
          unstashAll()
          log.info("closing...")
          context.unbecome()
        case msg =>
          log.info(s"stash by open... $msg")
          stash()

      }, discardOld = false)
    case msg =>
      log.info(s"stash... $msg")
      stash()
  }
}
