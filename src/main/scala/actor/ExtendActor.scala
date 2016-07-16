package actor

import akka.actor.{Actor, ActorLogging, ActorRef}

/**
  * Created by ikhoon on 2016. 7. 16..
  */

// protocol
case object GiveMeThings
final case class Give(thing: Any)

trait ProducerBehavior {
  this: Actor =>

  def produceBehavior : Receive = {
    case GiveMeThings => sender() ! Give("thing")
  }
}

trait ConsumerBehavior {
  this: Actor with ActorLogging =>

  def consumerBehavior: Receive = {
    case ref: ActorRef => ref ! GiveMeThings
    case Give(thing) => log.info("Got a thing! It is {}", thing)

  }
}


// create actor
class Producer extends Actor with ProducerBehavior {
  override def receive: Receive = produceBehavior
}

class Consumer extends Actor with ActorLogging with ConsumerBehavior {
  override def receive: Receive = consumerBehavior
}

// mixin all
class ProducerConsumer extends Actor with ActorLogging
  with ProducerBehavior with ConsumerBehavior {

  // compose
  override def receive: Receive = produceBehavior.orElse[Any, Unit](consumerBehavior)
}
