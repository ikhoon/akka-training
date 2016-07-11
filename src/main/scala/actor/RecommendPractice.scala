package actor

import akka.actor.{Actor, Props}

/**
  * Created by ikhoon on 2016. 7. 11..
  */

object AdderActor {

  def props(magicNumber: Int): Props = Props(new AdderActor(magicNumber))
}

class AdderActor(magicNumber: Int) extends Actor {

  override def receive: Receive = {
    case x: Int => sender() ! magicNumber + x
  }
}

class DoubleActor extends Actor {
  import akka.pattern.pipe
  import context.dispatcher
  // context.actorOf(new AdderActor(10), "adder") is not safe
  val adder = context.actorOf(AdderActor.props(10), "adder")

  override def receive: Receive = {
    case x : Int => adder forward x * 2
  }
}
