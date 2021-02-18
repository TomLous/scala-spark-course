// The overarching project
lazy val root = (project in file("."))
  .settings(
    commonSettings,
    testSettings,
    assemblySettings,
    runLocalSettings,
    name := "scala-spark-course"
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


// Assembly options
lazy val assemblySettings = Seq(
  assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false),
  assembly / assemblyOutputPath := baseDirectory.value / "../output" / (assembly / assemblyJarName).value,
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", _ @_*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  },
  assembly / logLevel := sbt.util.Level.Error,
  assembly / test := {},
  pomIncludeRepository := { _ => false}
)

// Include "provided" dependencies back to default run task
lazy val runLocalSettings = Seq(
  Compile / run := Defaults
    .runTask(
      fullClasspath in Compile,
      mainClass in (Compile, run),
      runner in (Compile, run)
    )
    .evaluated,
  Compile / runMain := Defaults
    .runTask(
      fullClasspath in Compile,
      mainClass in (Compile, run),
      runner in (Compile, run)
    )
    .evaluated
)




