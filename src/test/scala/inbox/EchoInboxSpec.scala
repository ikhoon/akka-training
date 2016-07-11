package inbox

import akka.actor.{ActorRef, ActorSystem, Inbox, Props}
import org.scalatest.WordSpec

/**
  * Created by ikhoon on 2016. 7. 11..
  */
class EchoInboxSpec extends WordSpec {

  "inbox message" should {
    "inbox receive" in {
      val system = ActorSystem()
      val actor: ActorRef = system.actorOf(Props[EchoActor])

      val inbox: Inbox = Inbox.create(system)
      import akka.pattern.ask
    }
  }
}
