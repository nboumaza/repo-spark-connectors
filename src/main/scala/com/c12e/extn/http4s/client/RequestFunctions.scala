package com.c12e.extn.http4s.client

import org.http4s.{EntityDecoder, Method, Request, Uri}


trait RequestFunctions {

  def apply(method: Method, uri: Uri): Request = Request(method, uri)

  def request(method: Method, uri: Uri): Request = Request(method, uri)

  def get(uri: Uri): Request = Request(Method.GET, uri)

  def post(uri: Uri): Request = Request(Method.POST, uri)

  def put(uri: Uri): Request = Request(Method.PUT, uri)

  def delete(uri: Uri): Request = Request(Method.DELETE, uri)

  def decode[B: EntityDecoder](detailed: DetailedResponse): ClientAction[B] =
    detailed.response.attemptAs[B]
      .leftMap(ClientFault malformedResponse detailed)

}


object RequestFunctions extends RequestFunctions