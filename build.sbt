import play.Project._

name := "account-ng"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

doc in Compile <<= target.map(_ / "none")