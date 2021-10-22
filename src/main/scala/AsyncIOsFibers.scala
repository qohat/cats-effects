import cats.effect.{Fiber, ExitCode, IO, IOApp}
import scala.concurrent.duration._

object AsyncIOsFibers extends IOApp {

  val firstIO: IO[Int] = IO(27)
  val secondIO: IO[String] = IO("Scala")

  def createFiber: Fiber[IO, Throwable, String] = ???

  extension [A] (io: IO[A])
    def debug: IO[A] = io.map { value =>
      println(s"[${Thread.currentThread().getName}] $value")
      value
    }

  def sameThread() = for {
    _ <- firstIO.debug
    _ <- secondIO.debug
  } yield ()

  val aFiber: IO[Fiber[IO, Throwable, Int]] = firstIO.debug.start

  def differentThreads() = for {
    _ <- aFiber.debug
    _ <- secondIO.debug
  } yield ()

  /**
   * 1 - success(IO(value))
   * 2 - errored
   * 3 - cancelled
   *
   * Is it not too similar to coroutines in Kotlin or Green Threads?
   */

  def runAnotherThread[A](io: IO[A]) = for {
    fib <- io.start
    result <- fib.join
  } yield result

  /**
   * 2 - errored
   * @return
   */
  def thrownOnAnotherThread() = for {
    fib <- IO.raiseError[Int](new RuntimeException("no number for you")).start
    result <- fib.join
  } yield result

  /**
   * 3- cancelled
   * @param args
   * @return
   */
   def cancelledThread() = {
     val task = IO("String").debug *> IO.sleep(1.second) *> IO("Done").debug

     for {
       fib <- task.start
       _ <- IO.sleep(500.millis) *> IO("Cancelling").debug
       _ <- fib.cancel
       result <- fib.join
     } yield result
   }

  def run(args: List[String]): IO[ExitCode] =
    cancelledThread().debug.as(ExitCode.Success)
}
