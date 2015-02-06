name := """play-bootstrap3"""

version := "0.4"

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.11.1", "2.10.4")

libraryDependencies ++= Seq(
  "org.webjars" % "jquery" % "2.1.3",
  "org.webjars" % "bootstrap" % "3.3.2")

lazy val root = (project in file(".")).enablePlugins(PlayScala)



scalariformSettings

//*******************************
// Maven settings
//*******************************

sonatypeSettings

publishMavenStyle := true

organization := "com.adrianhurt"

description := "This is a collection of input helpers and field constructors for Play Framework 2 to render Bootstrap 3 HTML code."

homepage := Some(url("http://play-bootstrap3.herokuapp.com"))

licenses := Seq("Apache License" -> url("https://github.com/adrianhurt/play-bootstrap3/blob/master/LICENSE"))

startYear := Some(2014)

publishTo := {
	val nexus = "https://oss.sonatype.org/"
	if (isSnapshot.value)
		Some("snapshots" at nexus + "content/repositories/snapshots")
	else
		Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
	<scm>
		<url>git@github.com:adrianhurt/play-bootstrap3.git</url>
		<connection>scm:git:git@github.com:adrianhurt/play-bootstrap3.git</connection>
	</scm>
	<developers>
		<developer>
			<id>adrianhurt</id>
			<name>Adrian Hurtado</name>
			<url>https://github.com/adrianhurt</url>
		</developer>
	</developers>
)

credentials += Credentials(Path.userHome / ".sbt" / "sonatype.credentials")
