// The overarching project
lazy val root = (project in file("."))
  .settings(
    commonSettings,
    testSettings,
    assemblySettings,
    runLocalSettings,
    name := "scala-spark-course",
    Compile / mainClass := ???
  )

// Libraries
val sparkVersion = "3.0.1"

val sparkLibs = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-streaming" % sparkVersion % Provided,
)

val testingLibs = Seq(
  "org.scalatest" %% "scalatest" % "3.2.3" % Test
)

// Settings for each module/subproject
lazy val commonSettings = Seq(
  organization := "xyz.graphiq",
  scalaVersion := "2.12.13",
  version := "0.1",
  libraryDependencies ++= sparkLibs ++ testingLibs,
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "UTF-8", // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
  )
)

// Test settings
lazy val testSettings = Seq(
  Test / testOptions += Tests.Argument("-oDT"),
  Test / parallelExecution := false
)

// Package settings
lazy val assemblySettings = Seq(
  // Assembly options
  assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false),
  assembly / assemblyOutputPath := baseDirectory.value / "../output" / (assembly / assemblyJarName).value,
  assembly / assemblyMergeStrategy := {
    case PathList(ps @ _*) if ps.last.startsWith("application.") && ps.last.endsWith(".conf") =>
      MergeStrategy.concat
    case PathList(ps @ _*) if ps.last.endsWith(".yaml") || ps.last.endsWith(".properties") =>
      MergeStrategy.first
    case PathList("META-INF", _ @_*) => MergeStrategy.discard
    case PathList(ps @ _*) if ps.last.startsWith("LICENSE") || ps.last.startsWith("NOTICE") =>
      MergeStrategy.discard
    case _ => MergeStrategy.first
  },
  assembly / logLevel := sbt.util.Level.Error,
  assembly / test := {},
  pomIncludeRepository := { _ =>
    false
  }
)

// Include "provided" dependencies back to default run task
// https://stackoverflow.com/questions/18838944/how-to-add-provided-dependencies-back-to-run-test-tasks-classpath/21803413#21803413
lazy val runLocalSettings = Seq(
  Compile / run := Defaults
    .runTask(
      fullClasspath in Compile,
      mainClass in (Compile, run),
      runner in (Compile, run)
    )
    .evaluated
)




