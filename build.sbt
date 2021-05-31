name := "demo"

version := "0.1-SNAPSHOT"

scalaVersion := "2.13.4"

resolvers += "Clojars" at "https://clojars.org/repo"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"    % "2.1.1",
  "org.typelevel" %% "spire"        % "0.17.0",
  "org.processing" % "core"         % "3.3.7",
  "org.processing" % "net"          % "3.3.7",
  "org.processing" % "video"        % "3.3.7",
  "org.processing" % "serial"       % "3.3.7",
  "org.processing" % "pdf"          % "3.3.7",
  "com.lowagie"    % "itext"        % "2.1.7",
  "quil"           % "jogl-all-fat" % "2.3.2"
)

dependencyOverrides += "com.lowagie" % "itext" % "2.1.7"
