name := """play-bootstrap3-sample"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.1"


lazy val root = (project in file(".")).enablePlugins(PlayScala)


libraryDependencies ++= Seq(
	"org.webjars" % "jquery" % "2.1.1",
	"org.webjars" % "bootstrap" % "3.2.0",
	"org.webjars" % "font-awesome" % "4.1.0",
	"com.adrianhurt" %% "play-bootstrap3" % "0.1-SNAPSHOT"
)



scalariformSettings
