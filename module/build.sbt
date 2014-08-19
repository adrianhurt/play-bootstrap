name := """play-bootstrap3"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.11.1", "2.10.4")


lazy val root = (project in file(".")).enablePlugins(PlayScala)



scalariformSettings

//*******************************
// Maven settings
//*******************************

organization := "com.adrianhurt"

publishMavenStyle := true