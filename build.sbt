name := "akka-training"

version := "1.0"

scalaVersion := "2.11.8"

lazy val versions = new {
  val akka = "2.4.8"
  val scalaTest  = "2.2.5"
  val logback = "1.1.7"
}

libraryDependencies ++= Seq(
  // akka
  "com.typesafe.akka" %% "akka-actor"      % versions.akka,
  "com.typesafe.akka" %% "akka-remote"     % versions.akka,
  "com.typesafe.akka" %% "akka-cluster"     % versions.akka,

  // akka http
  "com.typesafe.akka" %% "akka-stream" % versions.akka,
  "com.typesafe.akka" %% "akka-http-experimental" % versions.akka,
"com.typesafe.akka" %% "akka-http-core" % versions.akka,
  "com.typesafe.akka" %% "akka-http-testkit" % versions.akka % "test",

  "ch.qos.logback"      %  "logback-classic" % versions.logback,

  "com.typesafe.akka"   %% "akka-testkit"    % versions.akka      % "test",
  "org.scalatest"       %% "scalatest"       % versions.scalaTest % "test"
)
