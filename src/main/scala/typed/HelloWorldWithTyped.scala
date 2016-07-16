package typed

import akka.typed.ScalaDSL._
import akka.typed._
/**
  * Created by ikhoon on 2016. 7. 16..
  */
object HelloWorldWithTyped {

  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String)

  val greeter: Static[Greet] = Static[Greet] { (msg: Greet) =>
    println(s"Hello ${msg.whom}!")
    msg.replyTo ! Greeted(msg.whom)
  }

}
