import MainFuture.DefaulUserRepo.{User, users}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Random, Success}
/**
 * Just for testing Future behavior
 * Is Future really functional???
 */
object MainFuture:


  /**
   * I will implement
   */
  object DefaulUserRepo {
    val random = new Random() // Just for thread testing purposes
    case class User(id: String, email: String, age: Int)
    val users: Map[String, User] = Map(
      "xcvd-123-w" -> User("xcvd-123-w", "w@gmail.com", 29),
      "xcvd-123-w0" -> User("xcvd-123-w0", "w0@gmail.com", 30),
      "xcvd-123-w1" -> User("xcvd-123-w1", "w1@gmail.com", 24),
      "xcvd-123-w2" -> User("xcvd-123-w2", "w2@gmail.com", 27)
    )

    def find(id: String): Future[Option[User]] = Future {
      Thread.sleep(random.nextInt(400))
      users(id) match {
        case u: User => Some(u)
        case null => None
      }
    }

    def findAll(id: String): Future[List[User]] = Future {
      Thread.sleep(random.nextInt(600))
      users.values.toList
    }

    def save(user: User): Future[Unit] = Future {
      Thread.sleep(random.nextInt(300))
      users + (user.id -> user)
      ()
    }

    def update (id: String, user: User): Future[User] = Future {
      Thread.sleep(random.nextInt(200))
      users + (user.id -> user)
      user
    }

    def delete(id: String): Future[Unit] = Future {
      Thread.sleep(random.nextInt(500))
      users - id
      ()
    }
  }

  /**
   * Is future a Monad?
   * Left Identity: Future(x).flatMap(f) = f(x)
   * Right Identity: Future(x).flatMap(Future(x)) = Future(x)
   * Associativity: Future(x).flatMap(x => f(x).flatMap(g)) = Future(x).flatMap(f).flatMap(g)
   */
  val f: Option[User] => Future[Int] = {
    case Some(u) => Future(u.age * 2)
    case None => Future(0)
  }
  val g: Int => Future[String] = x => Future(s"${x} years old - Sr Scala developer")

  val monadValidation = for {
    leftIdentity <- DefaulUserRepo.find("xcvd-123-w").flatMap(f)
    leftIdentityEquality <- f(Some(users("xcvd-123-w")))
    satisfyLeftIdentity <- Future(leftIdentity == leftIdentityEquality)
    _ = println(s"Left Identity ${leftIdentity == leftIdentityEquality}")

    rightIdentity <- DefaulUserRepo.find("xcvd-123-w0").flatMap(Future(_))
    rightIdentityEquality <- DefaulUserRepo.find("xcvd-123-w0")
    satisfyRightIdentity <- Future(rightIdentity == rightIdentityEquality)
    _ = println(s"Right Identity ${rightIdentity == rightIdentityEquality}")

    associativity <- DefaulUserRepo.find("xcvd-123-w1").flatMap(u => f(u).flatMap(g))
    associativityEquality <- DefaulUserRepo.find("xcvd-123-w1").flatMap(f).flatMap(g)
    satisfyAssociativity <- Future(associativity == associativityEquality)
    _ = println(s"Associativity ${associativity == associativityEquality}")
  } yield Future(s"Is Future a monad? : ${satisfyLeftIdentity &&
    satisfyRightIdentity &&
    satisfyAssociativity }")

  /**
   * Is Future really purely functional
   *
   * Note that Scala’s Futures aren’t a great example of pure functional programming because they aren’t referentially transparent.
   * Future always computes and caches a result and there’s no way for us to tweak this behaviour.
   * This means we can get unpredictable results when we use Future to wrap side-effecting computations
   *
   * This kind of discrepancy makes it hard to reason about programs involving Futures and side-effects.
   * There also are other problematic aspects of Future's behaviour, such as the way it always starts
   * computations immediately rather than allowing the user to dictate when the program should run.
   *
   * Taken from Scala With Cats Book - https://www.scalawithcats.com/dist/scala-with-cats.html
   */

  val f1 = {
    val r = new Random(0L)
    val x = Future(r.nextInt)
    for {
      a <- x
      b <- x
    } yield (a, b)
  }

  // Same as f1, but I inlined `x`
  val f2 = {
    val r = new Random(0L)
    for {
      a <- Future(r.nextInt)
      b <- Future(r.nextInt)
    } yield (a, b)
  }

  def main(args: Array[String]): Unit = {
    Await.result(monadValidation, 1.second)

    f1.onComplete(println) // Success((-1155484576,-1155484576))
    f2.onComplete(println) // Success((-1155484576,-723955400))

    fut1.onComplete(println)
  }

  val fut = Future("Hello").andThen { case Success(v) => println(v) }

  val fut1 = Future("Hello").andThen { println(_) }

