package actor

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import org.scalatest.WordSpec

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by ikhoon on 2016. 7. 13..
  */
class AskPatternSpec extends WordSpec {
  "ask pattern" should {
    "ask return future" in {
      import akka.pattern.ask
      import akka.pattern.pipe
      implicit val timeout = Timeout(5 seconds)
      val system = ActorSystem()
      import system.dispatcher
      val actorA = system.actorOf(Props[ActorA])
      val actorB = system.actorOf(Props[ActorB])
      val actorC = system.actorOf(Props[ActorC])
      val actorD = system.actorOf(Props[ActorD])

      val future: Future[Result] = for {
        x <- ask(actorA, Request).mapTo[Int]
        s <- (actorB ask Request).mapTo[String]
        d <- (actorC ? Request).mapTo[Double]
      } yield Result(x, s, d)
      future.pipeTo(actorD)
      pipe(future) to actorD

      println(Await.result(future, Duration.Inf))
    }
  }
}
