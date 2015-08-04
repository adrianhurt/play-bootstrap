name := """play-bootstrap3-sample"""

version := "0.4"

scalaVersion := "2.11.6"

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
	"com.adrianhurt" %% "play-bootstrap3" % "0.4.5-P24-SNAPSHOT",
	"org.webjars" % "font-awesome" % "4.3.0-2",
	"org.webjars" % "bootstrap-datepicker" % "1.4.0"
)


scalariformSettings
