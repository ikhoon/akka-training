package actor

import akka.actor.{Actor, ActorLogging}
import akka.actor.Actor.Receive

/**
  * Created by ikhoon on 2016. 7. 12..
  */

final case class Result(x: Int, s: String, d: Double)
case object Request


class ActorA extends Actor {
  val x: Int = 1
  override def receive: Receive = {
    case Request =>
      sender() ! x * 10
  }
}

class ActorB extends Actor {

  val s: String = "i"

  override def receive: Receive = {
    case Request =>
      sender() ! s * 10
  }
}

class ActorC extends Actor {

  val d: Double = 2d

  override def receive: Receive = {
    case Request =>
      sender() ! math.pow(d, 10)
  }
}

class ActorD extends Actor with ActorLogging {
  override def receive: Receive = {
    case Result(x, s, d) => log.info(s"$x, $s, $d")
  }
}
