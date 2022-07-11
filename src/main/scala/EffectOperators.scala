import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.util.control.NoStackTrace

object EffectOperators {

  sealed trait Error extends NoStackTrace
  case object Error1 extends Error
  case object Error2 extends Error

  val oa = Some("Hi")
  val ob = Some("World")
  val a = Future.failed(Error1)
  val b = Future(ob)

  val c = for {
    fa <- a
    fb <- b
    result <- (fa, fb) match
      case (Some(a), Some(b)) => Future.successful(s"$a,$b")
      case _ => Future.failed(Error2)
  } yield result

  /* Impossible!!
  val c = for {
    tuple <- (a, b)
    result <- tuple match
      case (Some(a), Some(b)) => Future.successful(s"$a, $b")
      case _ => Future.failed(RuntimeException)
  } yield result
   */

  def tryF = Failure(Error1)
  def futTry: Future[Try[Long]] = Future(tryF).map(_.get).andThen {
    case Failure(e) => println(s"Yeah, it is a failure $e")
    case Success(v) => println(s"It is a succes but with a failure inside $v")
  }

  val futUnit: Future[Unit] = Future.successful("1L")

  def main(args: Array[String]) =
    println(Await.result(futUnit, 1.second))

}
