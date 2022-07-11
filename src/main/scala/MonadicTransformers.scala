import cats.data.{EitherT, OptionT}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

object MonadicTransformers {

  val optionT = OptionT.liftF(Future("Hello"))
    .subflatMap(Some(_))

}
