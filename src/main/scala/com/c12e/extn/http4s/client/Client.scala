package com.c12e.extn.http4s.client

import org.http4s.client.{Client => Http4sClient}
import org.http4s.{EntityEncoder, Request, Response}

import scalaz.EitherT
import scalaz.concurrent.Task
import scalaz.syntax.applicative._
import scalaz.syntax.std.boolean._


class Client private (client: Http4sClient) {

  def ask(request: Request): ClientAction[DetailedResponse] =
    execute(request, Task now request)

  def send[A : EntityEncoder]
  (request: Request, body: A): ClientAction[DetailedResponse] =
    execute(request, request withBody body)

  def shutdown: Task[Unit] = client.shutdown

  private def execute
  (rawRequest: Request, request: Task[Request])
  : ClientAction[DetailedResponse] =
    for {
      req <- Client.requestAction(rawRequest, request)
      res <- executeAction(req)
      _ <- Client.responseAction(req, res)
    } yield DetailedResponse(req, res)

  private def executeAction(request: Request): ClientAction[Response] =
    EitherT(client.toHttpService.run(request) attempt)
      .leftMap(ClientFault executionFault request)

}


object Client {

  val default: Client = new Client(org.http4s.client.blaze.defaultClient)

  def backedBy(backing: Http4sClient):Client = new Client(backing)

  private def requestAction
  (rawRequest: Request, request: Task[Request]): ClientAction[Request] =
    EitherT(request.attempt) leftMap ClientFault.requestFault(rawRequest)

  private def responseAction
  (request: Request, response: Response): ClientAction[Unit] = {
    val status = response.status
    status.isSuccess ?
      ().pure[ClientAction] |
      EitherT.left(Task now ClientFault.returnedFault(request, response))
  }

}