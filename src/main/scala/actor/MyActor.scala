package actor

import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.event.Logging

/**
  * Created by ikhoon on 2016. 7. 10..
  */
class MyActor extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case "test" => log.info("received test")
  }
}
