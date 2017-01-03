package com.c12e.repos.spark.mongo

import com.mongodb.spark.DefaultHelper.DefaultsTo

import com.mongodb.spark.config.{ReadConfig, WriteConfig}

import com.mongodb.spark.rdd.MongoRDD
import com.mongodb.spark.rdd.api.java.JavaMongoRDD
import com.mongodb.spark.sql.MongoInferSchema
import com.mongodb.spark.{MongoConnector, MongoSpark, NotNothing}

import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Dataset, Encoders, _}
import org.bson.conversions.Bson
import org.bson.{BsonDocument, Document}

import scala.collection.JavaConverters._
import scala.collection.Seq
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._


/**
  * Delegates to mongodb's MongoSpark
  */
object MongoRepo {

  private val DefaultMaxBatchSize = 512

  /**
    * The default source string for creating DataFrames from MongoDB
    */
  val defaultSource = "com.c12e.repos.spark.mongo.DefaultSource"

  /**
    * Create a builder for configuring MongoDB Repo
    *
    * @return a MongoSession Builder
    */
  def builder(): Builder = new Builder


  /**
    * Load data from MongoDB
    *
    * @param sc the Spark context containing the MongoDB connection configuration
    * @return a MongoRDD
    */
  def load[D: ClassTag](sc: SparkContext)(implicit e: D DefaultsTo Document): MongoRDD[D] = load(sc, ReadConfig(sc))

  /**
    * Load data from MongoDB
    *
    * @param sc the Spark context containing the MongoDB connection configuration
    * @return a MongoRDD
    */
  def load[D: ClassTag](sc: SparkContext, readConfig: ReadConfig)(implicit e: D DefaultsTo Document): MongoRDD[D] =
  builder().sparkContext(sc).readConfig(readConfig).build().toRDD[D]()

  /**
    * Load data from MongoDB
    *
    * @param sparkSession the SparkSession containing the MongoDB connection configuration
    * @tparam D The optional class defining the schema for the data
    * @return a MongoRDD
    */
  //TODO remove this option ?
  def load[D <: Product : TypeTag](sparkSession: SparkSession): DataFrame = MongoSpark.load(sparkSession)


  /**
    * Load data from MongoDB
    *
    * @param sparkSession the SparkSession containing the MongoDB connection configuration
    * @tparam D The optional class defining the schema for the data
    * @return a MongoRDD
    */
  def load[D <: Product : TypeTag](sparkSession: SparkSession, readConfig: ReadConfig): DataFrame = builder().sparkSession(sparkSession).readConfig(readConfig).build().toDF[D]()

  /**
    * Load data from MongoDB
    *
    * @param sparkSession the SparkSession containing the MongoDB connection configuration
    * @param clazz        the class of the data contained in the RDD
    * @tparam D The bean class defining the schema for the data
    * @return a MongoRDD
    */
  def load[D](sparkSession: SparkSession, readConfig: ReadConfig, clazz: Class[D]): Dataset[D] = builder().sparkSession(sparkSession).readConfig(readConfig).build().toDS(clazz)

  //TODO remove this option - We should always provide a writeConfig ?
  def save[D: ClassTag](rdd: RDD[D]): Unit = {

    //TODO insure we transform our input required params into mongo WriteConfig
    //get
   // MongoRepo.save(rdd, writeConfig)
    MongoSpark.save(rdd)
  }

  /**
    * Save data to MongoDB
    *
    * @param rdd         the RDD data to save to MongoDB
    * @param writeConfig the writeConfig
    * @tparam D the type of the data in the RDD
    */
  def save[D: ClassTag](rdd: RDD[D], writeConfig: WriteConfig): Unit = {
    //TODO insure we transform our input params into mongospark native params
    //translate our WriteConf to Mongo's
    MongoSpark.save(rdd, writeConfig)
  }

  /**
    * Save data to MongoDB
    *
    * Uses the `SparkConf` for the database and collection information
    *
    * '''Note:''' If the dataFrame contains an `_id` field the data will upserted and replace any existing documents in the collection.
    *
    * @param dataset the dataset to save to MongoDB
    * @tparam D the type of the data in the RDD
    * @since 1.1.0
    */
  def save[D](dataset: Dataset[D]): Unit = MongoSpark.save(dataset)


  /**
    * Save data to MongoDB
    *
    * Uses the `SparkConf` for the database and collection information
    *
    * @param dataFrameWriter the DataFrameWriter save to MongoDB
    */
  def save(dataFrameWriter: DataFrameWriter[_]): Unit = dataFrameWriter.format(defaultSource).save()

  /**
    * Save data to MongoDB
    *
    * @param dataFrameWriter the DataFrameWriter save to MongoDB
    * @param writeConfig     the writeConfig
    */
  def save(dataFrameWriter: DataFrameWriter[_], writeConfig: WriteConfig): Unit = dataFrameWriter.format(defaultSource).options(writeConfig.asOptions).save()

  /**
    * Creates a DataFrameReader with `MongoDB` as the source
    *
    * @param sparkSession the SparkSession
    * @return the DataFrameReader
    */
  def read(sparkSession: SparkSession): DataFrameReader = sparkSession.read.format(defaultSource)

  /**
    * Creates a DataFrameWriter with the `MongoDB` underlying output data source.
    *
    * @param dataset the Dataset to convert into a DataFrameWriter
    * @return the DataFrameWriter
    */
  def write[T](dataset: Dataset[T]): DataFrameWriter[T] = dataset.write.format(defaultSource)


  /**
    * The MongoRepo class
    *
    * '''Note:''' Creation of the class should be via MongoRepo.builder.
    *
    */
  case class MongoRepo(sparkSession: SparkSession, connector: MongoConnector, readConfig: ReadConfig, pipeline: Seq[BsonDocument]) {


    private def rdd[D: ClassTag]()(implicit e: D DefaultsTo Document): MongoRDD[D] = {
      new MongoRDD[D](sparkSession, sparkSession.sparkContext.broadcast(connector), readConfig, pipeline)
    }

    if (readConfig.registerSQLHelperFunctions) {
      UDF.registerFunctions(sparkSession)
    }

    /**
      * Creates a `RDD` for the collection
      *
      * @tparam D the datatype for the collection
      * @return a MongoRDD[D] MongoRdd of type D
      */
    def toRDD[D: ClassTag]()(implicit e: D DefaultsTo Document): MongoRDD[D] = rdd[D]

    /**
      * Creates a `JavaRDD` for the collection
      *
      * @return a JavaMongoRDD[Document]
      */
    def toJavaRDD(): JavaMongoRDD[Document] = rdd[Document].toJavaRDD()

    /**
      * Creates a `JavaRDD` for the collection
      *
      * @param clazz the class of the data contained in the RDD
      * @tparam D the type of the data in the RDD
      * @return the javaRDD
      */
    def toJavaRDD[D](clazz: Class[D]): JavaMongoRDD[D] = {
      implicit def ct: ClassTag[D] = ClassTag(clazz)
      rdd[D].toJavaRDD()
    }

    /**
      * Creates a `DataFrame` based on the schema derived from the optional type.
      *
      * '''Note:''' Prefer [[toDS[T<:Product]()*]] as computations will be more efficient.
      * The rdd must contain an `_id` for MongoDB versions < 3.2.
      *
      * @tparam T The optional type of the data from MongoDB, if not provided the schema will be inferred from the collection
      * @return a DataFrame
      */
    def toDF[T <: Product : TypeTag](): DataFrame = {
      val schema: StructType = MongoInferSchema.reflectSchema[T]() match {
        case Some(reflectedSchema) => reflectedSchema
        case None => MongoInferSchema(toBsonDocumentRDD)
      }
      toDF(schema)
    }

    /**
      * Creates a `DataFrame` based on the schema derived from the bean class.
      *
      * '''Note:''' Prefer [[toDS[T](beanClass:Class[T])*]] as computations will be more efficient.
      *
      * @param beanClass encapsulating the data from MongoDB
      * @tparam T The bean class type to shape the data from MongoDB into
      * @return a DataFrame
      */
    def toDF[T](beanClass: Class[T]): DataFrame = toDF(MongoInferSchema.reflectSchema[T](beanClass))

    /**
      * Creates a `DataFrame` based on the provided schema.
      *
      * @param schema the schema representing the DataFrame.
      * @return a DataFrame.
      */
    def toDF(schema: StructType): DataFrame = {
      sparkSession.read.format(defaultSource)
        .schema(schema)
        .options(readConfig.asOptions)
        .option("pipeline", pipeline.map(_.toJson).mkString("[", ",", "]"))
        .load()
    }

    /**
      * Creates a `Dataset` from the collection strongly typed to the provided case class.
      *
      * @tparam T The type of the data from MongoDB
      * @return
      */
    def toDS[T <: Product : TypeTag : NotNothing](): Dataset[T] = {
      val dataFrame: DataFrame = toDF[T]()
      import dataFrame.sqlContext.implicits._
      dataFrame.as[T]
    }

    /**
      * Creates a `Dataset` from the RDD strongly typed to the provided java bean.
      *
      * @tparam T The type of the data from MongoDB
      * @return
      */
    def toDS[T](beanClass: Class[T]): Dataset[T] = toDF[T](beanClass).as(Encoders.bean(beanClass))

    private def toBsonDocumentRDD: MongoRDD[BsonDocument] = {
      MongoSpark.builder()
        .sparkSession(sparkSession)
        .connector(connector)
        .readConfig(readConfig)
        .pipeline(pipeline)
        .build()
        .toRDD[BsonDocument]()
    }
  }

  /**
    * Builder for configuring and creating a MongoRepo
    *
    * It requires a `SparkSession` or the `SparkContext`
    */
  class Builder {

    private var sparkSession: Option[SparkSession] = None
    private var connector: Option[MongoConnector] = None
    private var readConfig: Option[ReadConfig] = None
    private var pipeline: Seq[Bson] = Nil
    private var options: collection.Map[String, String] = Map()

    def build(): MongoRepo = {
      require(sparkSession.isDefined, "The SparkSession must be set, either explicitly or via the SparkContext")

      val session = sparkSession.get

      val readConf = readConfig.isDefined match {
        case true => ReadConfig(options, readConfig)
        case false => ReadConfig(session.sparkContext.getConf, options)
      }

      val mongoConnector = connector.getOrElse(MongoConnector(readConf))

      //TODO
      //val bsonDocumentPipeline = pipeline.map(x => x.toBsonDocument(classOf[Document], mongoConnector.codecRegistry))
      val bsonDocumentPipeline =
      pipeline
        .map(x => x.toBsonDocument(classOf[Document],
          mongoConnector.withMongoClientDo({ client => client.getMongoClientOptions.getCodecRegistry })
        ))

      new MongoRepo(session, mongoConnector, readConf, bsonDocumentPipeline)
    }

    /**
      * Sets the SparkSession from the sparkSession
      *
      * @param sparkSession for the RDD
      */
    def sparkSession(sparkSession: SparkSession): Builder = {
      this.sparkSession = Option(sparkSession)
      this
    }

    /**
      * Sets the SparkSession from the sparkContext
      *
      * @param sparkContext for the RDD
      */
    def sparkContext(sparkContext: SparkContext): Builder = {

      this.sparkSession = Option(SparkSession.builder().config(sparkContext.getConf).getOrCreate())
      this
    }

    /**
      * Sets the SparkSession from the javaSparkContext
      *
      * @param javaSparkContext for the RDD
      */
    def javaSparkContext(javaSparkContext: JavaSparkContext): Builder = sparkContext(javaSparkContext.sc)


    /**
      * Append a configuration option
      *
      * These options can be used to configure all aspects of how to connect to MongoDB
      *
      * @param key   the configuration key
      * @param value the configuration value
      */
    def option(key: String, value: String): Builder = {
      this.options = this.options + (key -> value)
      this
    }

    /**
      * Set configuration options
      *
      * These options can configure all aspects of how to connect to MongoDB
      *
      * @param options the configuration options
      */
    def options(options: collection.Map[String, String]): Builder = {
      this.options = options
      this
    }

    /**
      * Set configuration options
      *
      * These options can configure all aspects of how to connect to MongoDB
      *
      * @param options the configuration options
      */
    def options(options: java.util.Map[String, String]): Builder = {
      this.options = options.asScala
      this
    }

    /**
      * Sets the [[com.mongodb.spark.MongoConnector]] to use
      *
      * @param connector the MongoConnector
      */
    def connector(connector: MongoConnector): Builder = {
      this.connector = Option(connector)
      this
    }

    /**
      * Sets the [[com.mongodb.spark.config.ReadConfig]] to use
      *
      * @param readConfig the readConfig
      */
    def readConfig(readConfig: ReadConfig): Builder = {
      this.readConfig = Option(readConfig)
      this
    }

    /**
      * Sets the aggregation pipeline to use
      *
      * @param pipeline the aggregation pipeline
      */
    def pipeline(pipeline: Seq[Bson]): Builder = {
      this.pipeline = pipeline
      this
    }
  }


}

