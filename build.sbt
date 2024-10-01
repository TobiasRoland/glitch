import sbt.Keys.libraryDependencies

val Version      = "0.1.0-SNAPSHOT"
val ScalaVersion = "3.3.4"

lazy val commonSettings = Seq(
  scalaVersion              := ScalaVersion,
  version                   := Version,
  organization              := "codes.mostly",
  idePackagePrefix          := Some("codes.mostly"),
  scalacOptions ++= Seq("-Xmax-inlines", "128"),
  scalacOptions ++= Seq("-no-indent", "-rewrite"),
  dependencyAllowPreRelease := true, // allows seeing pre-releases when running dependencyUpdates,
  libraryDependencies ++= Seq(
    "org.typelevel"      %% "cats-effect"     % "3.5.4",
    "org.typelevel"      %% "log4cats-slf4j"  % "2.7.0",
    "org.scodec"         %% "scodec-core"     % "2.3.1",
    "co.fs2"             %% "fs2-core"        % "3.11.0",
    "co.fs2"             %% "fs2-io"          % "3.11.0",
    "co.fs2"             %% "fs2-scodec"      % "3.11.0",
    "io.github.iltotore" %% "iron"            % "2.6.0",
    "org.scalatest"      %% "scalatest"       % "3.2.19"   % Test,
    "org.scalatestplus"  %% "scalacheck-1-18" % "3.2.19.0" % Test,
    "org.slf4j"           % "slf4j-simple"    % "2.0.16" // for now
  )
)

lazy val service = (project in file("app"))
  .settings(commonSettings*)
  .settings(
    name := "glitch",
    libraryDependencies ++= Seq(
      //
    )
  )

lazy val integration =
  (project in file("integration"))
    .dependsOn(service)
    .settings(commonSettings*)
    .settings(
      name           := "Integration Tests",
      publish / skip := true,
      libraryDependencies ++= Seq(
        //
      )
    )

addCommandAlias("compileAll", "clean;service/compile;integration/compile")
addCommandAlias("testAll", """eval scala.util.Properties.setProp("CI", "");service/test;integration/test""")
addCommandAlias("commitCheck", """dependencyUpdates;compileAll;scalafmtAll;testAll""")
addCommandAlias("cc", "commitCheck")
