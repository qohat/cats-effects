val scala3Version = "3.0.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "cats-effect",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.2.0",
      "org.scalacheck" %% "scalacheck" % "1.15.4" % "test",
      "com.github.alexarchambault" % "scalacheck-shapeless_1.15_2.13" % "1.3.0",
      "org.scalatest" %% "scalatest" % "3.2.12" % "test"
    )
  )