package com.qohat

import java.util.UUID

case class Team(name: String)
case class Game(t1: Team, t2: Team, t1Score: Int, t2Score: Int) {
  def winner: Option[Team] =
    if(t1Score > t2Score) Some(t1)
    else if(t2Score > t1Score) Some(t2)
    else None
}




