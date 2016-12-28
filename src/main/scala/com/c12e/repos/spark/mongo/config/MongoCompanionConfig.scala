package com.c12e.repos.spark.mongo.config

import java.util

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession

/**
  * The Mongo configuration base trait
  *
  * Defines companion object helper methods for creating MongoDB Config instances
  *
  * @since 1.0
  */
trait MongoCompanionConfig extends Serializable {

  /**
    * The type of the MongoDB Config
    */
  type Self

  /**
    * The configuration prefix string for the current configuration scope
    */
  val configPrefix: String

  /**
    * Create a configuration from the `sparkConf`
    *
    * Uses the prefixed properties that are set in the Spark configuration to create the config.
    *
    * @see [[configPrefix]]
    * @param sparkConf the spark configuration
    * @return the configuration
    */
   def apply(sparkConf: SparkConf): Self = apply(sparkConf, Map.empty[String, String])


  /**
    * Create a configuration from the `sparkConf`
    *
    * Uses the prefixed properties that are set in the Spark configuration to create the config.
    *
    * @see [[configPrefix]]
    * @param sparkConf the spark configuration
    * @param options overloaded parameters
    * @return the configuration
    */
  def apply(sparkConf: SparkConf, options: collection.Map[String, String]): Self =
        apply(getOptionsFromConf(sparkConf) ++ stripPrefix(options))


  /**
    * Create a configuration from the `sqlContext`
    *
    * Uses the prefixed properties that are set in the Spark configuration to create the config.
    *
    * @see [[configPrefix]]
    * @param sparkSession the SparkSession
    * @return the configuration
    */
  def apply(sparkSession: SparkSession): Self = apply(sparkSession.sparkContext.getConf)


  /**
    * Create a configuration from the values in the `Map`
    *
    * '''Note:''' Values in the map do not need to be prefixed with the [[configPrefix]].
    *
    * @param options a map of properties and their string values
    * @return the configuration
    */
  def apply(options: collection.Map[String, String]): Self = {
    apply(options)
  }


  /**
    * Create a configuration from the Java API using the `sparkConf`
    *
    * Uses the prefixed properties that are set in the Spark configuration to create the config.
    *
    * @param sparkConf the spark configuration
    * @return the configuration
    */
  def create(sparkConf: SparkConf): Self

  /**
    * Create a configuration easily from the Java API using the `JavaSparkContext`
    *
    * Uses the prefixed properties that are set in the Spark configuration to create the config.
    *
    * @see [[configPrefix]]
    * @param sparkSession the SparkSession
    * @return the configuration
    */
  def create(sparkSession: SparkSession): Self

  /**
    * Create a configuration from the `sparkConf`
    *
    *
    * @param sparkConf the spark configuration
    * @param options overloaded parameters
    * @return the configuration
    */
  def create(sparkConf: SparkConf, options: util.Map[String, String]): Self

  /**
    * Create a configuration easily from the Java API using the values in the `Map`
    *
    * @param options a map of properties and their string values
    * @return the configuration
    */
  def create(options: util.Map[String, String]): Self

  /**
    * Create a configuration easily from the Java API using the `JavaSparkContext`
    *
    * Uses the prefixed properties that are set in the Spark configuration to create the config.
    *
    * @see [[configPrefix]]
    * @param javaSparkContext the java spark context
    * @return the configuration
    */
  def create(javaSparkContext: JavaSparkContext): Self

  /**
    * Gets an options map from the `SparkConf`
    *
    * @param sparkConf the SparkConf
    * @return the options
    */
  def getOptionsFromConf(sparkConf: SparkConf): collection.Map[String, String] = stripPrefix(sparkConf.getAll.filter(_._1.startsWith(configPrefix)).toMap)

  /**
    * Strip the prefix from options
    *
    * @param options options that may contain the prefix
    * @return prefixLess options
    */
  def stripPrefix(options: collection.Map[String, String]): collection.Map[String, String] = options.map(kv => (kv._1.toLowerCase.stripPrefix(configPrefix), kv._2))

}
