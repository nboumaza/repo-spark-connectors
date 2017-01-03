package com.c12e.repos.spark.common.http


import com.c12e.extn.http4s.argonaut.NaiveArgonautCodec._
import com.c12e.extn.http4s.client._
import org.http4s.Uri.{Authority, RegName}
import org.http4s._
import org.http4s.client.blaze.{BlazeClientConfig, SimpleHttp1Client}
import org.http4s.util.CaseInsensitiveString

import scala.concurrent.duration._
import scalaz.{-\/, \/-}

object HttpClient {

  type MaybeRepo =  Either[ClientFault, Repository] // \/[ClientFault, Repository]

  val API_KEY: String = "X-CogScale-Key"
  val REPO_SERVICE_HOST = "api.foundation.insights.ai"
  val REPO_SERVICE_PORT = 80
  val REPO_ENDPOINT: String = "http://api.foundation.insights.ai/v1/repository/"
  val CON_INFO: String = "connection"
  val TIMEOUT_SECONDS: Int = 30

  val config_ = BlazeClientConfig.defaultConfig.copy(requestTimeout = TIMEOUT_SECONDS.second)
  val client_ = Client.backedBy(SimpleHttp1Client(config_))


  def shutdown() = client_.shutdown.unsafePerformSync


  /**
    * Fetches a repository connection details by api key and repo id
    *
    * @param repoId repository Id
    * @param apiKey api key
    * @return optional Repository if successful fetch None otherwise
    */
  def fetchRepoInfo(repoId: String, apiKey: String): Option[Repository] = {

    def getRepoInfo(repoId: String, apiKey: String):  MaybeRepo = {

      val uri_ = Uri(path = s"$REPO_ENDPOINT$repoId/$CON_INFO",
        authority = Some(Authority(host = RegName(REPO_SERVICE_HOST),
          port = Some(REPO_SERVICE_PORT))))

      val request = Request(
        method = Method.GET,
        uri = uri_,
        headers = Headers(Header.Raw(CaseInsensitiveString(API_KEY), apiKey))
      )

      client_
        .ask(request)
        .decode[Repository]
        .run
        .unsafePerformSync match {
        case \/-(repo) => shutdown(); Right(repo)
        case -\/(cf)   =>  shutdown(); Left(cf)   //TODO log
      }

    }

    getRepoInfo(repoId, apiKey) match {
      case Right(repo) =>  Some(repo)
      case Left(cf) => None
    }


  }


  //TODO remove test
    def main(args: Array[String]): Unit = {

      val repoId = "582dc1fe4a8a2e0011880682c"
      val apiKey = "4346c61a3d0d476caeb25b412cad4e0c"

      fetchRepoInfo(repoId, apiKey) match {
        case Some(repo) => attemptConnection(repo)
        case _          => handleError(s"Could not retrieve repository with id '$repoId")
      }


      def attemptConnection(repo: Repository) = {
        println(s"\n url = ${repo.server.host}:${repo.server.port}" +
          s"\n database = ${repo.database}")

      }

      def handleError(msg: String): Unit = throw new RuntimeException(msg)


    }



}