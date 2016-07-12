package actor

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.util.Timeout
import org.scalatest.WordSpec

/**
  * Created by ikhoon on 2016. 7. 13..
  */
class GracefulStopSpec extends WordSpec {

  "gracefull stop patten" should {
    "waiting until child is stop" in {
      val system = ActorSystem()
      import akka.pattern.ask
      import scala.concurrent.duration._
      implicit val timeout = Timeout(5 seconds)
      import system.dispatcher
      val manager: ActorRef = system.actorOf(Props[Manager])
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)

      manager ? Manager.Shutdown
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      manager ! "job"
      println("stop?")
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)
      (manager ? "job").mapTo[String].foreach(println)

    }

  }

}
