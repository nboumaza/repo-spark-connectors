package com.c12e.repos.spark

import org.apache.spark.{SparkConf, SparkContext}

/**
  * A helper for the tour
  */
trait TourHelper {

  def getSparkContext(args: Array[String]): SparkContext = {

    //val uri: String = args.headOption.getOrElse("mongodb://localhost/test.collection")

    val uri: String = args.headOption.getOrElse("mongodb://localhost/")

    val conf = new SparkConf()
      .setMaster("local[*]")
      //.set("spark.repo.input.uri", "mongodb://localhost/zazie.ztune")
      //.set("spark.repo.output.uri", "mongodb://localhost/zazie.ztune")
     // .set("spark.mongodb.input.uri", "mongodb://localhost/zazie.ztune")
     // .set("spark.mongodb.output.uri", "mongodb://localhost/zazie.ztune")
     .set("input.repo.id",  "582dc1fe4a8a2e0011880682c")
     .set("output.repo.id", "582dc1fe4a8a2e0011880682c")
      .set("api_key", "4346c61a3d0d476caeb25b412cad4e0c")
      .set("input.collection", "zazie")
      .set("output.collection", "zazie")  //assumes overwrite

      val sc = new SparkContext(conf)

      // MongoConnector(sc).withDatabaseDo(WriteConfig(sc), { db => db.drop() })

    sc
  }

  //fetchRepo()

}
