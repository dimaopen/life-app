import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "life-app",
    libraryDependencies += piccolo2d % Compile,
    libraryDependencies += piccolo2d_extras % Compile,
    libraryDependencies += rxJava % Compile,
    libraryDependencies += scalaTest % Test,
  )
