import cats.implicits._
import cats.data.ValidatedNec

sealed trait Code {
  val code: String
}

sealed trait ClientCode extends Code
sealed trait CampaignCode extends Code

case object DAAA extends ClientCode {
  override val code: String = "DAAA"
}

case object JBAE extends CampaignCode {
  override val code: String = "DAAA"
}

object Validated extends App {
  def validateOption: ValidatedNec[String, Int] =
    Option.empty.toValidNec("Error")

  println(validateOption)
}
