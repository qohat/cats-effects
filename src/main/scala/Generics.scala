import simulacrum.typeclass

sealed trait Queue {
  val name: String
}

object Queue {
  def queues[T <: Queue](group: String)(config: String)(toT: String => T): Set[T] =
    List(s"a$config.$group", s"b$config.$group")
      .map(toT)
      .toSet
}

case class Queue1(name: String) extends Queue
case class Queue2(name: String) extends Queue


val a: Set[Queue1] = Queue.queues[Queue1]("group")("config")(Queue1.apply)




object Stream {
  @typeclass trait EmailStream[A] {

  }
}




