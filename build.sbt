name := "scala-zlayer"
description := "scala zlayer"
version := "0.0.1"

val sharedSettings = Seq(
  scalacOptions ++= Seq(
    "-Xfatal-warnings",
    // "-Xprint:typer",
    "-Xlog-implicits"
  ),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % Versions.Scala_2,
    "dev.zio" %% "zio-test" % Versions.ZioVersion % Test
  ),
  // scalaVersion := Versions.Scala_3,
  scalaVersion := Versions.Scala_2,
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
)

lazy val backend = project
  .in(file("backend"))
  .settings(
    sharedSettings,
    Compile / run / mainClass := Some("base.Main"),
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(shared)

lazy val shared = project
  .in(file("shared"))
  .settings(
    sharedSettings
  )
