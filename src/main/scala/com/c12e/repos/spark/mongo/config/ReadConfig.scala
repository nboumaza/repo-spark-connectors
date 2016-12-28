
package com.c12e.repos.spark.mongo.config

import java.util

import com.c12e.repos.spark._
import com.c12e.repos.spark.common.config.RepoConfigProperty
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConverters._

/**
  * The `ReadConfig` companion object
  */
object ReadConfig extends  RepoConfigProperty with MongoCompanionConfig {

  type Self = ReadConfig
  override val configPrefix = "spark.mongodb.input."

  override def apply(options: collection.Map[String, String]): ReadConfig = {
    //TODO need to make these required
    val repoId = options.getOrElse(REPOSITORY_ID, "TODO")
    val apiKey = options.getOrElse(API_KEY, "TODO")
    val collection = options.getOrElse(COLLECTION, "TODO")

    /*new*/ ReadConfig(repoId, apiKey, collection)
  }


  /**
    * Creates a ReadConfigConfig
    *
    * @param repoId     repository Id
    * @param apiKey     apiKey
    * @param collectionName collection name to read from
    * @return the read config
    */
  def create(repoId: String, apiKey: String, collectionName: String):  ReadConfig = {

    new ReadConfig(repoId, apiKey, collectionName)
  }



  override def create(javaSparkContext: JavaSparkContext): ReadConfig = {
     //TODO
    // notNull("javaSparkContext", javaSparkContext)
    apply(javaSparkContext.getConf)
  }



  override def create(sparkSession: SparkSession): ReadConfig = {
    //TODO
    //notNull("sparkSession", sparkSession)
    apply(sparkSession)
  }

  override def create(sparkConf: SparkConf): ReadConfig = {
    notNull("sparkConf", sparkConf)
    apply(sparkConf)
  }

  override def create(options: util.Map[String, String]): ReadConfig = {
    //TODO
    //notNull("options", options)
    apply(options.asScala)
  }

  override def create(sparkConf: SparkConf, options: util.Map[String, String]): ReadConfig = {
    //TODO
    //notNull("sparkConf", sparkConf)
    //notNull("options", options)
    apply(sparkConf, options.asScala)
  }

}

/**
  * Read Configuration used when reading data from MongoDB
  *
  * @param repoId         repository Id
  * @param apiKey         api key
  * @param collectionName collection name
  */
case class ReadConfig( repoId: String, apiKey: String, collectionName: String) extends RepoClassConfig {
  //require(Try(connectionString.map(uri => new ConnectionString(uri))).isSuccess, s"Invalid uri: '${connectionString.get}'")

  type Self = ReadConfig


  override def withOptions(options: collection.Map[String, String]): ReadConfig = ReadConfig(options)

  override def asOptions: collection.Map[String, String] = {
    val options = Map("repoId" -> repoId, "apiKey" -> apiKey, "collection" -> collectionName)
    options

  }

  override def withOptions(options: util.Map[String, String]): ReadConfig = withOptions(options.asScala)

  override def asJavaOptions: util.Map[String, String] = asOptions.asJava
}

