name := "atm-service"

version := "0.1"

scalaVersion := "2.11.12"

val akkaVersion = "2.5.20"
val akkaHttpVersion = "10.1.7"

libraryDependencies ++= Seq(
  // Akka Streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // Akka HTTP
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  // MySQL
  "mysql" % "mysql-connector-java" % "5.1.48",
  // Dbcp2
  "org.apache.commons" % "commons-dbcp2" % "2.7.0",
  // Config
  "com.typesafe" % "config" % "1.3.2",
  // Logger
  "org.slf4j" % "slf4j-api" % "1.7.26",
  "org.slf4j" % "slf4j-simple" % "1.7.26",
  // JWT
  "com.pauldijou" %% "jwt-spray-json" % "4.2.0"
)
