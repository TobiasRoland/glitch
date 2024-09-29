val Version      = "0.1.0-SNAPSHOT"
val ScalaVersion = "3.3.4"

lazy val commonSettings = Seq(
  scalaVersion              := ScalaVersion,
  version                   := Version,
  organization              := "codes.mostly",
  idePackagePrefix          := Some("codes.mostly"),
  scalacOptions ++= Seq("-Xmax-inlines", "128"),
  scalacOptions ++= Seq("-no-indent", "-rewrite"),
  dependencyAllowPreRelease := true // allows seeing pre-releases when running dependencyUpdates
)

lazy val service = (project in file("service"))
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
    .settings(commonSettings * )
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
