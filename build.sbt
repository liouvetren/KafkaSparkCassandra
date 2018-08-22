name := "kafka-spark-cassandra-wordcount"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.2.0" % "provided"

libraryDependencies += ("org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.2.0").exclude("org.spark-project.spark", "unused")

libraryDependencies += ("com.datastax.spark" %% "spark-cassandra-connector" % "2.0.9").exclude("io.netty", "netty-handler")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := s"${name.value}_${scalaVersion.value}-2.2.0_${version.value}.jar"