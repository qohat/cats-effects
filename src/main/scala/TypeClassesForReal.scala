import simulacrum.typeclass

object TypeClassesForReal {

  @typeclass trait Show[A] {
    def show(a: A): String
  }

  case class MyClass(a: String, b: String)
  case class MyClass1(b: String)

  implicit class ShowOps[A: Show](a: A) {
    def show = Show[A].show(a)
  }

  object Show {

    def apply[A](implicit sh: Show[A]): Show[A] = sh

    implicit val stringCanShow: Show[String] =
      str => s"This is the $str"

    implicit val myClassCanShow: Show[MyClass] =
      cl => s"This is the (${cl.a}, ${cl.b})"

    implicit val myClass1CanShow: Show[MyClass1] =
      cl => s"This is the (${cl.b})"
  }

  def main(args: Array[String]): Unit = {
    val x = "Hi".show
    val y = MyClass("Hey", "What up").show
    println(s"$x, $y")
  }
}
