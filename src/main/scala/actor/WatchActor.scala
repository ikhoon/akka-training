package actor

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, Kill, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy, Terminated}

/**
  * Created by ikhoon on 2016. 7. 12..
  */
class WatchActor extends Actor with ActorLogging {

  var lastSender = context.system.deadLetters
  var child : ActorRef = _

  override def preStart(): Unit = {
    log.info("pre start...")
    child = context.actorOf(Props[WatchedActor], "watched")
    context.watch(child)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info("post restart...")
    child = context.actorOf(Props[WatchedActor], "watched")
    context.watch(child)
  }


  override def postStop(): Unit = {
    log.info("post stop...")
  }

  override def receive: Receive = {
    case "kill" =>
      log.info("received kill message")
      context.stop(child)
      lastSender = sender()
    case "restart" => throw new RuntimeException("receive restart message")
    case Terminated(c) => lastSender ! "finished"
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(){ case _ => Restart }

}

class WatchedActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case PoisonPill => log.info("receive poison pill on child")
    case Stop => log.info("receive stop on child")
    case Kill => log.info("receive kill on child")
    case _ => log.info("got some message on child")
  }

  override def postStop(): Unit = {
    log.info("stopping child...")
  }
}
