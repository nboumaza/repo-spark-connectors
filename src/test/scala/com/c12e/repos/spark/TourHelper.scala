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
      .setAppName("MongoSparkConnector")
      .set("spark.app.id", "MongoSparkConnector")
      //.set("spark.repo.input.uri", "mongodb://localhost/zazie.ztune")
      //.set("spark.repo.output.uri", "mongodb://localhost/zazie.ztune")
      .set("spark.mongodb.input.uri", "mongodb://localhost/zazie.ztune")
      .set("spark.mongodb.output.uri", "mongodb://localhost/zazie.ztune")
      .set("api_endpoint", "http//api.foundation.insights.ai/v1")
      .set("repo_id", "blabla")
      .set("api_key", "4346c61a3d0d476caeb25b412cad4e0c")

      //.set("api_endpoint“, "http://api.foundation.insights.ai”)

    //api_key=“123456789”, repo_id=“84f8323f8833455”, collection=“people"))

    val sc = new SparkContext(conf)
   // MongoConnector(sc).withDatabaseDo(WriteConfig(sc), { db => db.drop() })
    sc
  }

}
