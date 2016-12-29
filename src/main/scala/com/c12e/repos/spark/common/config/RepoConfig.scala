package com.c12e.repos.spark.common.config

/**
  *
  * @param repoId the repository Id
  * @param apiKey    the apiKey
  * @param collection  the collection to load/save from/to
  */
final case class RepoConfig(
  repoId: String,
  apiKey: String,
  collection: String
)


object RepoConfig extends RepoConfigProperty {

  /**
    *
    * @param params parameters {repoId, apiKey, collection }
    * @return a new instance of a RepoConf if the 3 required parameters are supplied, NONE otherwise
    */
  def fromParams(params: Map[String, String]): Option[RepoConfig] = {

    val repoId: Option[String] = params.get(REPOSITORY_ID)
    val apiKey: Option[String] = params.get(API_KEY)
    val collection: Option[String] = params.get(COLLECTION)

    val repoConf: Option[RepoConfig] = for {
      r <- repoId
      a <- apiKey
      c <- collection
    } yield RepoConfig(r,a,c)

    repoConf
  }
}
