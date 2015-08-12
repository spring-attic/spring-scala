organization := "com.github.norru"
name := "spring-scala"
version in ThisBuild := "1.0.0-SNAPSHOT"

val springVersion = "3.2.4.RELEASE"
crossPaths := true

scalaVersion := "2.11.7"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7", "-Xlint:-options")
scalacOptions ++= Seq("-target:jvm-1.7", "-feature", "-language:implicitConversions", "-language:reflectiveCalls", "-deprecation")
fork := true

EclipseKeys.withSource := true
EclipseKeys.eclipseOutput := Some("target")
EclipseKeys.withBundledScalaContainers := true
EclipseKeys.useProjectId := true
unmanagedSourceDirectories in Compile := (javaSource in Compile).value :: (scalaSource in Compile).value :: Nil
unmanagedSourceDirectories in Test := (javaSource in Test).value :: (scalaSource in Test).value :: Nil

resolvers := Seq(
	"springsource" at "http://repo.springsource.org/libs",
	"jitpack" at "https://jitpack.io",
	"springsource-snapshots" at "http://repo.springsource.org/libs-snapshot"
)

libraryDependencies ++=	Seq(
	// Spring
	"org.springframework" % "spring-aop" % springVersion % "optional",
	"org.springframework" % "spring-core" % springVersion,
	"org.springframework" % "spring-beans" % springVersion,
	"org.springframework" % "spring-context" % springVersion,
	"org.springframework" % "spring-jdbc" % springVersion % "optional",
	"org.springframework" % "spring-jms" % springVersion % "optional",
	"org.springframework" % "spring-web" % springVersion % "optional",
	"org.springframework" % "spring-test" % springVersion % "optional",

	"org.scala-lang" % "scala-library" % scalaVersion.value,
	"org.scala-lang" % "scala-reflect" % scalaVersion.value,

	//needs to be as separate jar file for scala 2.11
	"org.scala-lang.modules" %% "scala-xml" % "1.0.3",

	// Jackson,
	"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.2" % "optional",

	// Java EE
	"org.apache.geronimo.specs" % "geronimo-jms_1.1_spec" % "1.1" % "provided",
	"javax.servlet" % "servlet-api" % "2.5" % "provided",
	"javax.inject" % "javax.inject" % "1" % "provided",

	// Test
	"org.scalatest" %% "scalatest" % "2.2.2" % "test",
	"junit" % "junit" % "4.10" % "test",
	"org.hsqldb" % "hsqldb-j5" % "2.2.4" % "test",
	"log4j" % "log4j" % "1.2.16" % "test",
	"org.springframework" % "spring-aspects" % springVersion % "test"
)
