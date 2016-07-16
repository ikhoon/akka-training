package typed

import akka.util.Timeout
import org.scalatest.WordSpec

import scala.concurrent.Future

/**
  * Created by ikhoon on 2016. 7. 16..
  */
class HelloWorldWithTypedSpec extends WordSpec {
  import HelloWorldWithTyped._
  import akka.typed.AskPattern._
  import akka.typed._

  import concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global
  "typed actor" should {
    "it works" in {
      implicit val timeout = Timeout(5 seconds)
      val greet : ActorSystem[Greet] = ActorSystem("hello", Props(greeter))
      val future: Future[Greeted] = greet ? (Greet("world", _))
      for {
        greeting <- future.recover { case ex => ex.getMessage }
        done <- { println(s"result: $greeting"); greet.terminate() }
      } println("system terminated")
    }
  }
}
