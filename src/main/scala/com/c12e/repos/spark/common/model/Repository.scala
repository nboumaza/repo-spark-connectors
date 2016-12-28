package com.c12e.repos.spark.common.model

/**
  * Created by nboumaza on 12/2/16.
  */

object RepositoryClient {

  final case class SSLOptions(
                               ssl: Option[String],
                               sslValidate: Option[String],
                               checkServerIdentity: Option[String]
                             )

  final case class Server(
                           host: String,
                           port: String,
                           sslOptions: SSLOptions
                         )

  final case class Repository(
                               status: String,
                               database: String,
                               server: Server,
                               //T0DO will we ever care about below
                               username: String,
                               password: String
                             )

}


