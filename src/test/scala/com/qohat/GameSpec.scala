package com.qohat

import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

class GameSpec {

  val genTeam: Gen[Team] = for {
    name <- Gen.alphaStr
  } yield Team(name)

}
