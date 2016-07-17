package fault.tolerance

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec, WordSpecLike}

/**
  * Created by ikhoon on 2016. 7. 17..
  */
class SupervisorSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem(
    "SupervisorSpec",
    ConfigFactory.parseString(
      """
        akka {
          loggers = ["akka.testkit.TestEventListener"]
          loglevel = "WARNING"
        }
      """)
  ))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A supervisor" should {
    "apply the chosen strategy for its child" in {
      val supervisor = system.actorOf(Props[Supervisor], "supervisor")
      supervisor ! Props[Child]
      val child = expectMsgType[ActorRef]

      child ! 42
      child ! "get"
      expectMsg(42)

      child ! new ArithmeticException // resume
      child ! "get"
      expectMsg(42)

      child ! new NullPointerException // crash
      child ! "get"
      expectMsg(0)
    }
  }


}
