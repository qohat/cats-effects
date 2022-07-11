import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.concurrent.{Await, Future}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import concurrent.duration.*

object AkkaStreams {

  given system: ActorSystem = ActorSystem("MyActor")

  val source = Source(1 to 10)
  val sink = Sink.fold[Int, Int](0)(_ + _)

  // connect the Source to the Sink, obtaining a RunnableGraph
  val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

  // materialize the flow and get the value of the sink
  val sum: Future[Int] = runnable.run()
  val sum1: Future[Int] = source.runWith(sink)

  //source.map(_ => 0) // has no effect on source, since it's immutable
  val sum3 = source.runWith(Sink.fold(0)(_ + _))

  def main(args: Array[String]): Unit = {
    println(Await.result(sum3, 1.second))
  }
}
