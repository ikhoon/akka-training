package actor

import akka.actor.Actor

/**
  * Created by ikhoon on 2016. 7. 10..
  */

case class MyValueClass(v: Int) extends AnyVal

class ValueActor(value: MyValueClass) extends Actor {
  override def receive : Receive = {
    case multiplier : Long => sender() ! value.v * multiplier
  }
}

class DefaultValueActor(a: Int, b: Int = 5) extends Actor {
  override def receive : Receive = {
    case x : Int  => sender() ! (a * b) + x
  }
}

class DefaultValueActor2(a: Int = 10) extends Actor {
  override def receive : Receive = {
    case x : Int  => sender() ! a + x
  }
}
