import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.actor.ActorSystem

sealed trait Message {
  def toEmailMessage: EmailMessage
}
case class Message1(a: String, b: String) extends Message {
  override def toEmailMessage: EmailMessage =
    Welcome(a, b)
}
case class Message2(a: String, b: String, c: Option[String]) extends Message {
  override def toEmailMessage: EmailMessage =
    NewAccount(a, b, c.getOrElse(""))
}

sealed trait EmailMessage {
  val id: String
  val email: String
}

case class Welcome(id: String, email: String) extends EmailMessage
case class NewAccount(id: String, email: String, firstName: String) extends EmailMessage

sealed trait Queue

trait Consumer {
  def consume(q: Queue): Unit
}

object Consumer {
  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val messages: Source[Message, NotUsed] = Source(Message1("a", "b") :: Message2("a", "b", None) :: Nil)

  def make: Consumer =
    new Consumer:
      override def consume(q: Queue): Unit =
        messages
          .map(_.toEmailMessage)
          .runForeach(println)
}

object MainStreams {
  def main(args: Array[String]): Unit =
    Consumer
      .make
      .consume(new Queue {})
}


