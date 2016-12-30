package com.c12e.repos.spark

import com.c12e.repos.spark.mongo.MongoRepo
import com.mongodb.spark.config.WriteConfig

/**
  * The Introduction code example see docs/0-introduction.md
  */
object Introduction extends TourHelper {

  /**
    * Run this main method to see the output of this example or copy the main code into the spark shell
    *
    * @param args takes an optional single argument for the connection string
    * @throws Throwable if an operation fails
    */
  def main(args: Array[String]): Unit = {

    val sc = getSparkContext(args)

    //REMOVE below
    val conf = sc.getConf.getAll
    println("Conf params >>>>>>>>>>>>>")
    conf.foreach(println)
    //---------

    // Saving data from an RDD to MongoDB
    import org.bson.Document
    val documents = sc.parallelize((1 to 10).map(i => Document.parse(s"{field: $i}")))

   // Saving data with a custom WriteConfig
  // import com.mongodb.spark.config._
    val writeConfig = WriteConfig(Map("uri" -> "mongodb://localhost/", "database" -> "test", "collection" -> "spark", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))
    MongoRepo.save(documents, writeConfig )


    //MongoRepo.save(documents)

// Saving data with a custom WriteConfig
/*import com.mongodb.spark.config._
val writeConfig = WriteConfig(Map("collection" -> "spark", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))

val sparkDocuments = sc.parallelize((1 to 10).map(i => Document.parse(s"{spark: $i}")))
MongoSpark.save(sparkDocuments, writeConfig)

// Loading and analyzing data from MongoDB
val rdd = MongoSpark.load(sc)
println(rdd.count)
println(rdd.first.toJson)

// Loading data with a custom ReadConfig
val readConfig = ReadConfig(Map("collection" -> "spark", "readPreference.name" -> "secondaryPreferred"), Some(ReadConfig(sc)))
val customRdd = MongoSpark.load(sc, readConfig)
println(customRdd.count)
println(customRdd.first.toJson)

// Filtering an rdd in Spark
val filteredRdd = rdd.filter(doc => doc.getInteger("test") > 5)
println(filteredRdd.count)
println(filteredRdd.first.toJson)

// Filtering an rdd using an aggregation pipeline before passing to Spark
val aggregatedRdd = rdd.withPipeline(Seq(Document.parse("{ $match: { test : { $gt : 5 } } }")))
println(aggregatedRdd.count)
println(aggregatedRdd.first.toJson)

*/


}

}
