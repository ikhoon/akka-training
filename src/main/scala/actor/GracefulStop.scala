package actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props, Terminated}
import akka.actor.Actor.Receive
import akka.util.Timeout

/**
  * Created by ikhoon on 2016. 7. 13..
  */

object Manager {
  case object Shutdown
}

class Manager extends Actor with ActorLogging {
  import Manager._
  import akka.pattern.ask
  import akka.pattern.pipe
  import context.dispatcher
  import scala.concurrent.duration._
  implicit val timeout = Timeout(5 seconds)
  val worker = context.actorOf(Props[Worker], "worker")
  override def receive: Receive = {
    case "job" =>
      val target = sender()
      (worker ? "dododo").pipeTo(target)
    case Shutdown =>
      worker ! PoisonPill
      context become shuttingDown
  }

  def shuttingDown: Receive = {
    case "job" => sender() ! "shutting down, go out"
    case Terminated(`worker`) =>
      log.info("child is stopped")
      context stop self
  }
}

class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case x: String => log.info(x + " " + x);
  }

  override def postStop(): Unit = {
    log.info("stop worker")
  }

}
