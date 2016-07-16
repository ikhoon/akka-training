package actor

import akka.actor.{ActorSystem, Props}
import org.scalatest.WordSpec

/**
  * Created by ikhoon on 2016. 7. 16..
  */
class ExtendActorSpec extends WordSpec {

  "an actor extend behavior from trait" should {
    val system = ActorSystem()
    "it works" in {

      val producer = system.actorOf(Props[Producer], "producer")
      val consumer = system.actorOf(Props[Consumer], "consumer")

      consumer ! producer
    }
    "all in one" in {
      val producerConsumer = system.actorOf(Props[ProducerConsumer], "producer-consumer")
      producerConsumer ! producerConsumer

    }
  }
}
