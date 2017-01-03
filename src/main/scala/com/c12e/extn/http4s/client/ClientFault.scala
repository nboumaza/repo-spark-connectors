package com.c12e.extn.http4s.client

import org.http4s.{DecodeFailure, Request, Response}


sealed trait ClientFault
final case class RequestFault(request: Request, cause: Throwable)
  extends ClientFault
final case class MalformedResponse
(request: Request, response: Response, details: DecodeFailure)
  extends ClientFault
final case class ReturnedFault(request: Request, response: Response)
  extends ClientFault
final case class ExecutionFault(request: Request, cause: Throwable)
  extends ClientFault


object ClientFault {

  def requestFault(request: Request)(cause: Throwable): ClientFault =
    RequestFault(request, cause)

  def malformedResponse(details: DetailedResponse)(decodeFailure: DecodeFailure): ClientFault =
    MalformedResponse(details.request, details.response, decodeFailure)

  def returnedFault(request: Request, response: Response): ClientFault =
    ReturnedFault(request, response)

  def executionFault(request: Request)(cause: Throwable): ClientFault =
    ExecutionFault(request, cause)

}