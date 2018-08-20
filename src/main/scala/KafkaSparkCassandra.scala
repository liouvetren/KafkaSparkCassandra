// word count kafkasparkcassandra
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.SparkConf

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import com.datastax.driver.core.Cluster
import com.datastax.spark.connector._

import java.util.Date

object KafkaSparkCassandra {

  def main(args: Array[String]) {

    // // // Usage: $0 topicSet batch_interval cassandra_host
    // // val topicSet       = Set[String](args(1))
    // // val batch_interval = args(2).toLong
    // // val cassandra_host = args(3)

    // // spark streaming context with cassandra storage
    // val batch_interval = 1000 // millisecond
    // val cassandraHost = "cassandra.database"
    // val sparkConf = new SparkConf()
    //                 .setAppName("WordCount")
    //                 .set("spark.cassandra.connection.host", "cassandra.database")
    // val ssc = new StreamingContext(sparkConf, Duration(batch_interval))
    // ssc.sparkContext.setLogLevel("ERROR")

    // kafka connection
    val kafkaBroker = "kafka.kafka:9092"
    val topicSet = Array("twitterdata")
    val kafkaParams = Map[String, Object](
        "bootstrap.servers"  -> kafkaBroker,
        // "key.deserializer"   -> classOf[StringDeserializer],
        // "value.deserializer" -> classOf[StringDeserializer],
        "group.id"           -> "wordcount",
        "auto.offset.reset"  -> "latest",
        "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    // val dstream = KafkaUtils.createDirectStream[String,String](
    //     ssc,
    //     PreferConsistent,
    //     Subscribe[String,String](topicSet,kafkaParams)
    // )
   
    // // connect directly to Cassandra from the driver to create the keyspace
    
    // val db_sess = Cluster.builder().addContactPoint(cassandraHost).build().connect()

    // db_sess.execute("CREATE KEYSPACE IF NOT EXISTS twitterdata WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }")
    // db_sess.execute("CREATE TABLE IF NOT EXISTS twitterdata.word_count (word text, ts timestamp, count int, PRIMARY KEY(word, ts)) ")
    // db_sess.execute("TRUNCATE twitterdata.word_count") // purge data at the start
    // db_sess.close()


    // // Create the processing logic
    // // the spark processing isn't actually run until the streaming context is started
    // // it will then run once for each batch interval

    // // Get the lines, split them into words, count the words and print
    // val wordCounts = dstream.map(_.value) // split the message into lines
    //   .flatMap(_.split(" ")) //split into words
    //   .filter(w => w.length() > 0) // remove any empty words caused by double spaces
    //   .map(w => (w, 1L)).reduceByKey(_ + _) // count by word
    //   .map({case (w,c) => (w,new Date().getTime,c)}) // add the current time to the tuple for saving


    // // Save each RDD to the ic_example.word_count table in Cassandra
    // wordCounts.foreachRDD(rdd => {
    //   rdd.saveToCassandra("twitterdata","word_count")
    // })

    // // Now we have set up the processing logic it's time to do some processing
    // ssc.start() // start the streaming context
    // ssc.awaitTermination() // block while the context is running (until it's stopped by the timer)

  }
}
