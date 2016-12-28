package com.c12e.repos.spark.mongo.config

import java.util

import com.c12e.repos.spark._
import com.c12e.repos.spark.common.config.RepoConfigProperty
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConverters._


/**
  * The `WriteConfig` companion object
  *
  */
object WriteConfig extends RepoConfigProperty with MongoCompanionConfig{

  type Self = WriteConfig

  override val configPrefix = "spark.mongodb.output."


 /* def apply(repoId: String, apiKey: String, collectionName: String): WriteConfig = {
    WriteConfig(repoId, apiKey, collectionName)
  }*/


  /**
    * Create a configuration from the values in the `Map`
    *
    * @param options config options
    * @return a WriteConfig
    */
  override def apply(options: collection.Map[String, String]): WriteConfig = {

    //TODO need to make these required
    val repoId = options.getOrElse(REPOSITORY_ID, "TODO")
    val apiKey = options.getOrElse(API_KEY, "TODO")
    val collection = options.getOrElse(COLLECTION, "TODO")

    /*new*/  WriteConfig(repoId, apiKey, collection)

  }



  /*
    * Creates a WriteConfig
    *
    * @param repoId     repository Id
    * @param apiKey     apiKey
    * @param collectionName collection name to write to
    * @return the write config
    */
   def create(repoId: String, apiKey: String, collectionName: String): WriteConfig = {
     //TODO
     /*notNull(REPOSITORY_ID, repoId)
    notNull(API_KEY, apiKey)
    notNull(COLLECTION, collectionName)
    */
    apply(repoId, apiKey, collectionName)
  }


  override  def create(javaSparkContext: JavaSparkContext): WriteConfig = {
     //TODO
     //notNull(JAVA_SPARK_CONTEXT, javaSparkContext)
     apply(javaSparkContext.getConf)
   }

   override def create(sparkConf: SparkConf): WriteConfig = {
     //TODO
     //notNull("sparkConf", sparkConf)
     apply(sparkConf)
   }

   override def create(options: util.Map[String, String]): WriteConfig = {
     //TODO
     //notNull("options", options)
     apply(options.asScala)
   }



   override def create(sparkConf: SparkConf, options: util.Map[String, String]): WriteConfig = {
     //TODO
     //notNull("sparkConf", sparkConf)
     //notNull("options", options)
     apply(sparkConf, options.asScala)
   }



   override def create(sparkSession: SparkSession): WriteConfig = {
     //TODO
     notNull("sparkSession", sparkSession)
     apply(sparkSession)
   }


}


/**
  * Write Configuration for writes to MongoDB
  *
  * @param repoId         repository Id
  * @param apiKey         api key
  * @param collectionName collection name
  */
case class WriteConfig(repoId: String, apiKey: String, collectionName: String) extends RepoClassConfig {

  // require(Try(connectionString.map(uri => new ConnectionString(uri))).isSuccess, s"Invalid uri: '${connectionString.get}'")

  type Self = WriteConfig

  //TODO remove below
  // override def withOption(key: String, value: String): WriteConfig = WriteConfig(this.asOptions)

  override def withOptions(options: collection.Map[String, String]): WriteConfig = WriteConfig(options)

  override def asOptions: collection.Map[String, String] = {
    val options = Map("repoId" -> repoId, "apiKey" -> apiKey, "collection" -> collectionName)
    options

  }

  override def withOptions(options: util.Map[String, String]): WriteConfig = withOptions(options.asScala)

  override def asJavaOptions: util.Map[String, String] = asOptions.asJava


}
