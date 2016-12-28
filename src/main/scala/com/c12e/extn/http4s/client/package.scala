package com.c12e.extn.http4s

import org.http4s.UriFunctions

import scalaz.EitherT
import scalaz.concurrent.Task


package object client extends RequestFunctions with UriFunctions {

  type ClientAction[A] = EitherT[Task, ClientFault, A]

}