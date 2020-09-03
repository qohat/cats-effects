name := "cats-effect"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.0.0"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds", // or import scala.language.higherKinds
  "-Ymacro-annotations", // for newtype and simulacrum
)