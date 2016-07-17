package fault.tolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}

import scala.concurrent.duration._

/**
  * Created by ikhoon on 2016. 7. 17..
  */
class Supervisor extends Actor with ActorLogging {

  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _ : ArithmeticException => Resume
    case _ : NullPointerException => Restart
    case _ : IllegalArgumentException => Stop
    case _ : Exception => Escalate
  }

  override def receive: Receive = {
    case p: Props =>
      log.info(s"received props: $p")
      sender() ! context.actorOf(p)
  }
}

class Child extends Actor with ActorLogging {
  var state = 0
  override def receive: Receive = {
    case ex: Exception => throw ex
    case x: Int =>
      log.info(s"set $x")
      state = x
    case "get" =>
      log.info(s"get $state")
      sender() ! state
  }

  override def postRestart(reason: Throwable): Unit = {
    println(s"post restart : $reason, state : $state")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println(s"pre restart : $reason, msg : $message, $state")
  }
}
