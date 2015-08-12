**This is not the official build from Pivotal. I have forked the project to ultimately get a stable release for both Scala 2.10 and 2.11** --norru

# Spring Scala

The goal of Spring Scala is to make it easier to use the Spring framework in Scala.

**Note that Spring Scala is currently not maintained by Pivotal, as such there will be no further releases nor bug fixes coming from Pivotal.**

Please refer to the original project's [documentation on the wiki](https://github.com/SpringSource/spring-scala/wiki)

## Installation

For Maven users:

    <repository>
        <id>jitpack.io</id>
        <name>Jitpack public repository</name>
        <url>https://jitpack.io</url>
    </repository>
    ...
    <dependency>
        <groupId>com.github.norru</groupId>
        <artifactId>spring-scala</artifactId>
        <version>1.0.0</version>
    </dependency>https://jitpack.io
    
For sbt users:

    libraryDependencies ++= Seq(
		"org.springframework.scala" % "spring-scala"	% "1.0.0",
	)
    
### compile and test, build all jars, distribution zips and docs
`./sbt publish`

## Original Documentation

You can find out the original information about this project on the [wiki](https://github.com/SpringSource/spring-scala/wiki)

## Original Issue Tracking

Spring Scala uses [JIRA](https://jira.springsource.org/browse/SCALA) for issue tracking purposes

## Original License

Spring Scala is [Apache 2.0 licensed](http://www.apache.org/licenses/LICENSE-2.0.html).
