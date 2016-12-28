package com.c12e.extn.http4s.client

import org.http4s.{ EntityDecoder, Request, Response }
import scalaz.syntax.bind._


final case class DetailedResponse(request: Request, response: Response)


object DetailedResponse {

  implicit class ClientActionOps[A](action: ClientAction[DetailedResponse]) {
    def decode[B: EntityDecoder]: ClientAction[B] =
      action >>= RequestFunctions.decode[B]
  }

}