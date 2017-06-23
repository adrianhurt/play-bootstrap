name := """play-bootstrap-sample"""

version := "1.1"

scalaVersion := "2.11.8"

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  filters,
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P25-B4-SNAPSHOT",
  "org.webjars" % "bootstrap" % "4.0.0-alpha.6-1" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.2.1",
  "org.webjars" % "font-awesome" % "4.7.0",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0" exclude("org.webjars", "bootstrap")
)


scalariformSettings
