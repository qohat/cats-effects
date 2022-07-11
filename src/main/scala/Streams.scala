import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.actor.ActorSystem

sealed trait Message
case class Message1(a: String, b: String) extends Message
case class Message2(a: String, b: String, c: Option[String]) extends Message

sealed trait EmailMessage {
  val id: String
  val email: String
}

case class Welcome(id: String, email: String) extends EmailMessage
case class NewAccount(id: String, email: String, firstName: String) extends EmailMessage

sealed trait Queue

trait Consumer[-T] {
  def consume(q: Queue): Unit
}

object Consumer {
  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val messages: Source[Message, NotUsed] = Source(Message1("a", "b") :: Message2("a", "b", None) :: Nil)

  def make[T]: Consumer[T] =
    new Consumer[T]:
      override def consume(q: Queue): Unit =
        messages
          .runForeach(println)
}

object MainStreams {
  def main(args: Array[String]): Unit =
    Consumer
      .make[Message1]
      .consume(new Queue {})
}


