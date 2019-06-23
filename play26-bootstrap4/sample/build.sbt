name := """play-bootstrap-sample"""

version := "1.5"

scalaVersion := "2.12.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  guice,
  filters,
  "com.adrianhurt" %% "play-bootstrap" % "1.5-P26-B4-RC-SNAPSHOT",
  "org.webjars" % "bootstrap" % "4.3.1" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.3.1-2",
  "org.webjars" % "font-awesome" % "4.7.0",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0" exclude("org.webjars", "bootstrap")
)


scalariformSettings
