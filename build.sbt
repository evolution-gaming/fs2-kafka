val ReleaseTag = """^release/([\d\.]+a?)$""".r

lazy val contributors = Seq(
 "pchlupacek" -> "Pavel Chlupáček"
)

lazy val commonSettings = Seq(
   organization := "com.evolutiongaming",
   bintrayOrganization := Some("evolutiongaming"),
   scalaVersion := "2.12.6",
   crossScalaVersions := Seq("2.11.12",  "2.12.6"),
   scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-language:postfixOps",
    "-Xfatal-warnings",
    "-Yno-adapted-args",
    "-Ywarn-value-discard",
    "-Ywarn-unused-import"
   ),
   javaOptions += "-Djava.net.preferIPv4Stack=true",
   scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)},
   scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
   libraryDependencies ++= Seq(
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
    , "org.scalatest" %% "scalatest" % "3.0.0" % "test"
    , "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
    , "co.fs2" %% "fs2-core" % "1.0.2"
    , "co.fs2" %% "fs2-io" % "1.0.2"
    , "com.evolutiongaming" %% "protocol-kafka" % "0.3.17-evolution2"
    , "com.spinoco" %% "fs2-log-core" % "0.1.0"
   ),
   scmInfo := Some(ScmInfo(url("https://github.com/Spinoco/fs2-kafka"), "git@github.com:Spinoco/fs2-kafka.git")),
   homepage := None,
   licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
   resolvers += Resolver.bintrayRepo("evolutiongaming", "maven"),
   initialCommands := s"""
    import fs2._
    import fs2.util._
    import spinoco.fs2.kafka
    import spinoco.fs2.kafka._
    import spinoco.protocol.kafka._
    import scala.concurrent.duration._
  """
) ++ testSettings ++ scaladocSettings ++ publishingSettings ++ releaseSettings

lazy val testSettings = Seq(
  parallelExecution in Test := false,
  fork in Test := true,
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
  publishArtifact in Test := true
)

lazy val scaladocSettings = Seq(
   scalacOptions in (Compile, doc) ++= Seq(
    "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-implicits",
    "-implicits-show-all"
  ),
   scalacOptions in (Compile, doc) ~= { _ filterNot { _ == "-Xfatal-warnings" } },
   autoAPIMappings := true
)

lazy val publishingSettings = Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  pomExtra := {
    <url>https://github.com/Spinoco/fs2-kafka</url>
    <developers>
      {for ((username, name) <- contributors) yield
      <developer>
        <id>{username}</id>
        <name>{name}</name>
        <url>http://github.com/{username}</url>
      </developer>
      }
    </developers>
  },
  pomPostProcess := { node =>
   import scala.xml._
   import scala.xml.transform._
   def stripIf(f: Node => Boolean) = new RewriteRule {
     override def transform(n: Node) =
       if (f(n)) NodeSeq.Empty else n
   }
   val stripTestScope = stripIf { n => n.label == "dependency" && (n \ "scope").text == "test" }
   new RuleTransformer(stripTestScope).transform(node)(0)
  }
)

lazy val releaseSettings = Seq(
  releaseCrossBuild := true
)

lazy val `f2-kafka` =
  project.in(file("."))
  .settings(commonSettings)
  .settings(
    name := "fs2-kafka"
  )
 
 

