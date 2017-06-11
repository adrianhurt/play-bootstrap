name := """play-bootstrap-sample"""

version := "1.1"

scalaVersion := "2.12.2"

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  guice,
  "com.adrianhurt" %% "play-bootstrap" % "1.1.2-P26-B4-SNAPSHOT",
  "org.webjars" % "font-awesome" % "4.7.0",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0"
)


scalariformSettings
