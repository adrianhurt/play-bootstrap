name := """play-bootstrap-sample"""

version := "1.0"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P23-B3-SNAPSHOT",
  "org.webjars" % "font-awesome" % "4.5.0",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0"
)


scalariformSettings
