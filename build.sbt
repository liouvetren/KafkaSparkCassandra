name := "kafka-spark-cassandra-wordcount"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.0" % "provided"

libraryDependencies += ("org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.0").exclude("org.spark-project.spark", "unused")

libraryDependencies += ("com.datastax.spark" %% "spark-cassandra-connector" % "2.3.0").exclude("io.netty", "netty-handler")




assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
