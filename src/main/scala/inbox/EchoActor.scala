package inbox

import akka.actor.Actor

/**
  * Created by ikhoon on 2016. 7. 11..
  */
class EchoActor extends Actor {

  override def receive: Receive = {
    case x: String => sender() ! x + " " + x
  }
}
