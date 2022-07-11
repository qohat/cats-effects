import cats.implicits.*
import cats.data.ValidatedNec

import scala.annotation.tailrec

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

sealed trait DomainError
case object IsNotZip extends DomainError
case object IsNotCityState extends DomainError

case class Location(value: String)

sealed trait CoordType
case class Zip(location: Location) extends CoordType
case class CityState(location: Location) extends CoordType

object LocationValidator {
  private def validateZip(location: Location): Either[DomainError, Zip] =
    Either.cond(
      location.value.matches("^[a-zA-Z0-9]+$"),
      Zip(location),
      IsNotZip
    )

  private def validateCityState(location: Location): Either[DomainError, CityState] =
    Either.cond(
      location.value.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"),
      CityState(location),
      IsNotCityState
    )

  def validateLocation(location: Location): Either[List[DomainError], CoordType] = {
    val (errors, coords) =
      List(validateZip(location), validateCityState(location))
        .partitionMap(identity)

      coords
      .headOption
      .toRight(errors)
  }
}

object MainValidated extends App {
  println(LocationValidator.validateLocation("my location"))
}