import sbt._

import Keys._

import play.PlayScala

//import PlayKeys._


name := "shorter"

version := "1.0"

scalaVersion := "2.11.2"

resolvers += "Typesafe" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies += "com.typesafe.play" %% "play" % "2.3.5"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.0.6"

//libraryDependencies += "org.specs2" %% "specs2" % "2.4.6" % "test"

//libraryDependencies += "org.scalatestplus" %% "play" % "1.1.0" % "test"

//play.Project.playScalaSettings

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//lazy val shorter = (project in file(".")) //.enablePlugins(PlayScala)