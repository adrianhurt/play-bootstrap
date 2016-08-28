name := """play-bootstrap-sample"""

version := "1.1"

scalaVersion := "2.11.8"

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B4",
  "org.webjars" % "font-awesome" % "4.6.3",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0"
)


scalariformSettings
