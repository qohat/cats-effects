import cats.effect.{IO, Resource}
import cats.implicits._
import java.io._
object Main {

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO(new FileInputStream(f))                         // build
    } { inStream =>
      IO(inStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.make {
      IO(new FileOutputStream(f))                         // build
    } { outStream =>
      IO(outStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  def copy(origin: File, destination: File): IO[Long] = {
    val inIO: IO[InputStream]  = IO(new FileInputStream(origin))
    val outIO:IO[OutputStream] = IO(new FileOutputStream(destination))

    (inIO, outIO)             // Stage 1: Getting resources
      .tupled                  // From (IO[InputStream], IO[OutputStream]) to IO[(InputStream, OutputStream)]
      .bracket{
        case (in, out) =>
          transfer(in, out)    // Stage 2: Using resources (for copying data, in this case)
      } {
        case (in, out) =>      // Stage 3: Freeing resources
          (IO(in.close()), IO(out.close()))
            .tupled              // From (IO[Unit], IO[Unit]) to IO[(Unit, Unit)]
            .handleErrorWith( _ => IO.unit).void
      }
  }

  def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
    for {
      amount <- IO(origin.read(buffer, 0, buffer.size))
      count  <- if(amount > -1) IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
      else IO.pure(acc) // End of read stream reached (by java.io.InputStream contract), nothing to write
    } yield count // Returns the actual amount of bytes transmitted

  def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
    for {
      buffer <- IO(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
      total  <- transmit(origin, destination, buffer, 0L)
    } yield total


}
