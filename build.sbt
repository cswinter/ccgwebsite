import sbt.Project.projectToRef

lazy val clients = Seq(client)
lazy val scalaV = "2.11.8"

lazy val sharedDependencies =
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % "0.5.5",
    "com.lihaoyi" %%% "upickle" % "0.4.1",
    // Requires publishLocal at 63a36f156e76cd7d8950d378c149b2423f90defb
    "org.codecraftgame" %%% "codecraft" % "0.6.1" changing()
  )

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.5.0",
    "org.apache.commons" % "commons-email" % "1.4",
    "joda-time" % "joda-time" % "2.8.2",
    "com.amazonaws" % "aws-java-sdk" % "1.10.29",
    "com.typesafe.play" % "anorm_2.11" % "2.5.0",
    jdbc,
    "org.scalatest" %% "scalatest" % "2.2.1" % "test",
    "org.scalatestplus" %% "play" % "1.4.0-M3" % "test"
  ),
  sharedDependencies
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := false,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "com.github.japgolly.scalajs-react" %%% "core" % "0.10.3",
    "com.github.japgolly.scalajs-react" %%% "extra" % "0.10.3",
    "com.github.chandu0101.scalajs-react-components" %%% "core" % "0.4.0"
  ),
  sharedDependencies,
  jsDependencies ++= Seq(
    "org.webjars.bower" % "react" % "0.14.3"
      /         "react-with-addons.js"
      minified  "react-with-addons.min.js"
      commonJSName "React",

    "org.webjars.bower" % "react" % "0.14.3"
    /         "react-dom.js"
    minified  "react-dom.min.js"
    dependsOn "react-with-addons.js"
    commonJSName "ReactDOM"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation"),
    sharedDependencies
  ).
  jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

