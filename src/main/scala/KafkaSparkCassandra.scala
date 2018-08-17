// word count kafkasparkcassandra

// Imports for loading in the kafka.properties file
import java.io.FileReader
import java.util.Properties
import scala.collection.JavaConversions._

// Basic Spark imports
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

// Spark SQL Cassandra imports
import com.datastax.spark.connector._

// Spark Streaming + Kafka imports
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

// Cassandra Java driver imports
import com.datastax.driver.core.Cluster

// Date import for processing logic
import java.util.Date

object KafkaSparkCassandra {

  def main(args: Array[String]) {

    // Usage: $0 topicSet batch_interval cassandra_host
    val topicSet       = Set[String](args(1))
    val batch_interval = args(2).toLong
    val cassandra_host = args(3)

    // read the configuration file
    val sparkConf = new SparkConf().setAppName("WordCount")

    // connect directly to Cassandra from the driver to create the keyspace
    val cluster = Cluster.builder().addContactPoint(cassandra_host).build()
    val session = cluster.connect()
    session.execute("CREATE KEYSPACE IF NOT EXISTS twitterdata WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };")
    session.execute("CREATE TABLE IF NOT EXISTS twitterdata.word_count (word text, ts timestamp, count int, PRIMARY KEY(word, ts)) ")
    session.execute("TRUNCATE twitterdata.word_count") // purge data at the start
    session.close()

    // Create spark streaming context with millisecond batch interval
    val ssc = new StreamingContext(sparkConf, Duration(batch_interval))

    // Set the logging level to reduce log message spam
    ssc.sparkContext.setLogLevel("ERROR")

    // create a timer that we will use to stop the processing after 60 seconds so we can print some results
    val timer = new Thread() {
      override def run() {
        Thread.sleep(1000 * 30)
        ssc.stop()
      }
    }

    // load the kafka.properties file
    val kafkaProps = new Properties()
    kafkaProps.load(new FileReader("kafka.properties"))
    val kafkaParams = kafkaProps.toMap[String, String]
    val messages = KafkaUtils.createDirectStream(ssc,PreferConsistent,Subscribe[String,String](topicSet,kafkaParams))

    // Create the processing logic
    // the spark processing isn't actually run until the streaming context is started
    // it will then run once for each batch interval

    // Get the lines, split them into words, count the words and print
    val wordCounts = messages.map(_.value) // split the message into lines
      .flatMap(_.split(" ")) //split into words
      .filter(w => w.length() > 0) // remove any empty words caused by double spaces
      .map(w => (w, 1L)).reduceByKey(_ + _) // count by word
      .map({case (w,c) => (w,new Date().getTime,c)}) // add the current time to the tuple for saving


    // Save each RDD to the ic_example.word_count table in Cassandra
    wordCounts.foreachRDD(rdd => {
      rdd.saveToCassandra("twitterdata","word_count")
    })

    // Now we have set up the processing logic it's time to do some processing
    ssc.start() // start the streaming context
    timer.start()
    ssc.awaitTermination() // block while the context is running (until it's stopped by the timer)
    ssc.stop() // this additional stop seems to be required

    // Get the results using spark SQL
    val sc = new SparkContext(sparkConf) // create a new spark core context
    val rdd1 = sc.cassandraTable("twitterdata", "word_count")
    rdd1.take(100).foreach(println)
    sc.stop()

    System.exit(0)
  }
}
