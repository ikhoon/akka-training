package typed

import akka.typed._
import akka.typed.ScalaDSL._
import akka.typed.AskPattern._

/**
  * Created by ikhoon on 2016. 7. 17..
  */

// protocol
sealed trait Command
final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends Command

sealed trait SessionEvent
final case class SessionGranted(handle: ActorRef[PostMessage]) extends SessionEvent
final case class SessionDenied(reason: String) extends SessionEvent
final case class MessagePosted(screenName: String, message: String) extends SessionEvent

final case class PostMessage(message: String)

private final case class PostSessionMessage(screenName: String, message: String) extends Command

// behavior
object ChatRoom {
  val behavior: Behavior[GetSession] =
    ContextAware[Command] { ctx =>
      var sessions = List.empty[ActorRef[SessionEvent]]

      Static {
        case GetSession(screenName, client) =>
          println(s"screen : $screenName, get session : $sessions")
          sessions ::= client
          val wrapper = ctx.spawnAdapter {
            p: PostMessage => PostSessionMessage(screenName, p.message)
          }
          client ! SessionGranted(wrapper)
        case PostSessionMessage(screenName, message) =>
          println(s"screen : $screenName post session message : $message")
          val mp = MessagePosted(screenName, message)
          sessions foreach(_ ! mp)
      }

    }.narrow

}
