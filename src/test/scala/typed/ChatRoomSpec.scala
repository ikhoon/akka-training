package typed

import akka.typed._
import akka.typed.ScalaDSL._
import org.scalatest.WordSpec

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by ikhoon on 2016. 7. 17..
  */
class ChatRoomSpec extends WordSpec {

  "chat room" should {
    "get session and post message" in {
      import ChatRoom._
      val gabbler: Behavior[SessionEvent] =
        Total {
          case SessionDenied(reason) =>
            println(s"cannot start chat room session: $reason")
            Stopped
          case SessionGranted(handle) =>
            println(s"session is granted : $handle")
            handle ! PostMessage("hello world")
            Same
          case MessagePosted(screenName, message) =>
            println(s"message has been posted by '$screenName': $message")
            Stopped
        }

      val main : Behavior[Unit] =
        Full {
          case Sig(ctx, PreStart) =>
            val chatRoom = ctx.spawn(Props(ChatRoom.behavior), "chatroom")
            val gabblerRef = ctx.spawn(Props(gabbler), "gabbler")
            ctx.watch(gabblerRef)
            chatRoom ! GetSession("ol' Gabbler", gabblerRef)
            Same
          case Sig(_, Terminated(ref)) =>
            Stopped
        }

      val system = ActorSystem("chatRoomDemo", Props(main))
      Await.result(system.whenTerminated, 1 second)
    }
  }
}
