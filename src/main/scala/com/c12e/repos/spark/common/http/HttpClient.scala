package com.c12e.repos.spark.common.http


import com.c12e.extn.http4s.argonaut.NaiveArgonautCodec._
import com.c12e.extn.http4s.client.{Client, ClientFault}
import org.http4s.Uri.{Authority, RegName}
import org.http4s._
import org.http4s.client.blaze.{BlazeClientConfig, PooledHttp1Client}
import org.http4s.util.CaseInsensitiveString

import scala.concurrent.duration._
import scalaz.{-\/, \/, \/-}

object HttpClient {

  type MaybeRepo = ClientFault \/ Repository

  val API_Key: String = "X-CogScale-Key"
  val REPO_SERVICE_HOST= "api.foundation.insights.ai"
  val REPO_SERVICE_PORT = 80
  val REPO_ENDPPOINT: String = "http://api.foundation.insights.ai/v1/repository/"
  val CON_INFO: String = "connection"
  val TIMEOUT_SECONDS: Int = 30

  val config_ = BlazeClientConfig.defaultConfig.copy(requestTimeout = TIMEOUT_SECONDS.second)
  val client_ = Client.backedBy(PooledHttp1Client(config = config_))

  /**
    *
    */
  def shutdown: Unit = client_.shutdown

  /**
    * Fetches a repository connection details by api key and repo id
    *
    * @param repoId repository Id
    * @param apiKey api key
    * @return  repository info
    */
  def getRepoInfo(repoId: String, apiKey: String): MaybeRepo = {

    val uri_ = Uri(path = s"${REPO_ENDPPOINT}${repoId}/${CON_INFO}",
      authority = Some(Authority(host = RegName(REPO_SERVICE_HOST),
        port = Some(REPO_SERVICE_PORT) )))

    val request = Request(
      method = Method.GET,
      uri = uri_,
      headers = Headers(Header.Raw(CaseInsensitiveString(API_Key), apiKey))
    )

    val response = client_
      .ask(request)
      .decode[Repository]
      .run
      .unsafePerformSync

    response match {
      case \/-(repository) => println("Goooooooddddddd")
      case -\/(error) =>  println("Ouch!")

    }
    response


  }


  /*private def handleResponse[A](response: ClientFault \/ A) = {
    response.fold(notFoundToNone,a => success(some(a)))
  }*/

  def main(args: Array[String]): Unit = {

    val maybeRepo = getRepoInfo("582dc1fe4a8a2e0011880682",  "4346c61a3d0d476caeb25b412cad4e0c")

    shutdown

    println(maybeRepo.getOrElse("bla"))





  }

}