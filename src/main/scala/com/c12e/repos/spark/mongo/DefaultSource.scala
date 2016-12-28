package com.c12e.repos.spark.mongo

import com.c12e.repos.spark.common.config.RepoConfig
import org.apache.spark.sql.sources.BaseRelation
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}

/**
  * mongodb type repository DataSource
  * Leverages com.mongodb.spark.sql.DefaultSource native connector for spark
  *
  */
class DefaultSource extends com.mongodb.spark.sql.DefaultSource {

  override def shortName(): String = "com.c12e.repos.spark.mongo"

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {

   /* val repoConf = getRepoConf(parameters)
    if (repoConf == None){
      //TODO handle invalid repos config args
    }
    val rc = repoConf.get

   println(s"repoConf ${rc}")
   */
    val conf = sqlContext.sparkSession.conf.getAll
    conf.foreach(println)

    println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")

    super.createRelation(sqlContext, mode, parameters, data)
  }


  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {

    //TODO
    super.createRelation(sqlContext, parameters)
  }


  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): BaseRelation = {
    //TODO
    super.createRelation(sqlContext, parameters, schema)
  }



  //TODO refactor common needed stuff to trait
  def getRepoConfig(params: Map[String, String]) : Option[RepoConfig] = RepoConfig.fromParams(params)


}
