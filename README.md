# Spring Scala

The goal of Spring Scala is to make it easier to use the Spring framework in Scala.

**Note that Spring Scala is currently not maintained by Pivotal, as such there will be no further releases nor bug fixes coming from Pivotal.**

Currently, the two main areas of focus are:

* Wiring up Scala classes as Spring Beans, both in traditional [XML](https://github.com/SpringSource/spring-scala/wiki/Defining-Scala-Beans-in-Spring-XML) as well as [Scala](https://github.com/SpringSource/spring-scala/wiki/Functional-Bean-Configuration)
* Provide [Scala-friendly](https://github.com/SpringSource/spring-scala/wiki/Using-Spring-Templates-in-Scala) wrappers for the Spring templates

For more information, please refer to the [documentation on the wiki](https://github.com/SpringSource/spring-scala/wiki)

## Installation

Milestones of Spring Scala are available for download at our milestone repository, http://repo.springsource.org/milestone.

For Maven users:

    <repository>
        <id>repository.springsource.milestone</id>
        <name>SpringSource Milestone Repository</name>
        <url>http://repo.springsource.org/milestone</url>
    </repository>
    ...
    <dependency>
        <groupId>org.springframework.scala</groupId>
        <artifactId>spring-scala_2.10</artifactId>
        <version>1.0.0.RC1</version>
    </dependency>
    
## Snapshots

Nightly snapshots of Spring Scala are available for download at our snapshot repository, http://repo.springsource.org/snapshot.

For Maven users:

    <repository>
        <id>repository.springsource.snapshot</id>
        <name>SpringSource Snapshot Repository</name>
        <url>http://repo.springsource.org/snapshot</url>
    </repository>
    ...
    <dependency>
        <groupId>org.springframework.scala</groupId>
        <artifactId>spring-scala_2.10</artifactId>
        <version>1.0.0.BUILD-SNAPSHOT</version>
    </dependency>

## Building from Source

Spring Scala uses a [Gradle](http://gradle.org)-based build system.
In the instructions below, [`./gradlew`](http://vimeo.com/34436402) is invoked from the root of the source tree and serves as a cross-platform, self-contained bootstrap mechanism for the build.
The only prerequisites are [Git](http://help.github.com/set-up-git-redirect) and JDK 1.7+.

### check out sources
`git clone git://github.com/SpringSource/spring-scala.git`

### compile and test, build all jars, distribution zips and docs
`./gradlew build`

### install all spring-\* jars into your local Maven cache
`./gradlew install`

... and discover more commands with `./gradlew tasks`. See also the [Gradle build and release FAQ](https://github.com/SpringSource/spring-framework/wiki/Gradle-build-and-release-FAQ).

## Documentation

You can find out more information about this project on the [wiki](https://github.com/SpringSource/spring-scala/wiki)

## Issue Tracking

Spring Scala uses [JIRA](https://jira.springsource.org/browse/SCALA) for issue tracking purposes

## License

Spring Scala is [Apache 2.0 licensed](http://www.apache.org/licenses/LICENSE-2.0.html).
