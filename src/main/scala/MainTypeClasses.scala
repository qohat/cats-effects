object MainTypeClasses:

  /**
   * Understanding
   * - Semigroup
   * - Monoid
   * - Functor
   * - Applicative
   * - Monad
   * - Semigroupal
   * - Apply
   * - ApplicativeError
   * - MonadError
   */

  /**
   * Set or type plus a combination function
   * The combination function will take two args of the same type T and produce a third wihich corresponds to that type T as well
   * @tparam T
   */
  trait Semigroup[T] {
    def combine(x: T, y: T): T
  }

  object Semigroup {
    def apply[T](using semigroup: Semigroup[T]): Semigroup[T] = semigroup
  }

  /**
   * The Semigroup with a twist
   * the identity function can be combine with other element and should return the particular element
   * conmutative multiplication property x + empty = empty + x = x
   * @tparam T
   */
  trait Monoid[T] extends Semigroup[T] {
    def empty: T
  }

  object Monoid {
    def apply[T](using monoid: Monoid[T]): Monoid[T] = monoid
  }

  /**
   * Following cats strategy to organize instances
   * Is better organizing the instances by type implemented instead type class
   */
  object IntInstances {
    given intSemigroup: Semigroup[Int] with {
      override def combine(x: Int, y: Int): Int = x + y
    }

    given intMonoid: Monoid[Int] with {
      override def empty: Int = 0
      override def combine(x: Int, y: Int): Int = x + y
    }
  }

  object StringInstances {
    given stringSemigroup: Semigroup[String] with {
      override def combine(x: String, y: String): String = x + y
    }

    given stringMonoid: Monoid[String] with {
      override def empty: String = ""
      override def combine(x: String, y: String): String = x + y
    }
  }

  object SemigroupSyntax {
    extension [T](a: T)
      def |+|(b: T)(using semigroup: Semigroup[T]): T = semigroup.combine(a, b)
  }

  import IntInstances.given
  import StringInstances.given
  import SemigroupSyntax._

  val semiInt = 1 |+| 2
  val semiString = "Scala" |+| " 3"

  println(s"$semiInt --- $semiString")

  /**
   *Manipulating inmutable data and tranforming a type A to type B
   */
  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  object Functor {
    def apply[F[_], A, B](using functor: Functor[F]) = functor
  }

  object OptionInstances {
    given optionFunctor: Functor[Option] with {
      override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
    }
  }

  object FunctorSyntax {
    extension [F[_], A, B] (fa: F[A])(using functor: Functor[F])
      def map(f: A => B) = functor.map(fa)(f)
  }

  import OptionInstances.given
  import FunctorSyntax._
  val optionFunc: Option[String] = Some(12).map(x => x * 2).map(_.toString)
  println(s"$optionFunc")

  trait Semigroupal[F[_]] {
    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
  }

  trait Apply[F[_]] extends Semigroupal[F] with Functor[F] {
    def ap[A, B](fab: F[A => B], fa: F[A]): F[B]

    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] = {
      val myF: A => B => (A, B) = (a: A) => (b: B) => (a, b)
      val fab: F[B => (A, B)] = map(fa)(myF)
      ap(fab, fb)
    }

    def mapN[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] =
      map(product(fa, fb)) {
        case (a,b) => f(a,b)
      }
  }
  /**
   * Its capability is wraping a value A into an F type constructor through a pure method
   * @tparam F
   */
  trait Applicative[F[_]] extends Apply[F] {
    def pure[A](a: A): F[A]

    def map[A, B](fa: F[A])(f: A => B): F[B] =
      ap(pure(f), fa)
  }

  trait ApplicativeError[F[_], E] extends Applicative[F] {
    def raiseError[A](error: E): F[A]
  }

  /**
   * FlatMap extends Functor
   * flatMap method chain computations in a powerfull way - can do it for any sort (Type)
   * map can chain computations as well but more generic
   * @tparam F
   */
  trait FlatMap[F[_]] extends Functor[F] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  }

  /**
   * A monad should satisfy three laws
   * 1. left-identity pure(x).flatMap(f) = f(x) : If I flatMap a pure instance of a Monad with an element x applying a function f()
   * the result should be f applied to element x
   * 2. right-identity monadInstance.flatMap(pure()) = monadInstance : If I flatMap a Monad instance using the pure function,
   * the result should be the same MonadInstance
   * 3. Associativity m.flatMap(x => f(x).flatMap(g)) = m.flatMap(f).flatMap(g) : The way we group the flatMap application
   * shouldn't change the result
   * @tparam F
   */
  trait Monad[F[_]] extends Applicative[F] with FlatMap[F] {
    /**
     * We can comment this method and creating a trait called FlatMap, so if Monad needs the flatMap method Monad should extends FlatMap trait
     */
    //def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    /**
     * We can implement our map method for free using the flatMap from FlatMap and pure from Applicative
     */
    override def map[A, B](fa: F[A])(f: A => B): F[B] = flatMap(fa)(a => pure(f(a)))
  }

  trait MonadError[F[_], E] extends ApplicativeError[F, E] with Monad[F]





