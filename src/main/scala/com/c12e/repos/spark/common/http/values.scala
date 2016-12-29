package com.c12e.repos.spark.common.http

import argonaut.Argonaut._
import argonaut.CodecJson

final case class Repository(status: String,
                            database: String,
                            server: Server,
                            username: String,
                            password: String
                           )

object Repository {

  implicit def RepositoryCodecJson: CodecJson[Repository] =
    casecodec5(Repository.apply, Repository.unapply)("status", "database", "server", "username", "password")
}

final case class Options(ssl: String, sslValidate: String, checkServerIdentity: String)

object Options {
  implicit def OptionCodecJson: CodecJson[Options] =
    casecodec3(Options.apply, Options.unapply)("ssl", "sslValidate", "checkServerIdentity")
}


final case class Server(host: String, port: String, options: Options)

object Server {
  implicit def ServerCodecJson: CodecJson[Server] =
    casecodec3(Server.apply, Server.unapply)("host", "port", "options")
}

/* http4s */
final case class RepositoryFault(message: String) extends AnyVal



