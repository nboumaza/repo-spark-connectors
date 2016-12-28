package com.c12e.extn.http4s.argonaut

import argonaut.{DecodeJson, EncodeJson}
import org.http4s.argonaut.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}


  trait NaiveArgonautCodec {
    implicit def decoderInstance[A: DecodeJson]: EntityDecoder[A] = jsonOf[A]
    implicit def encoderInstance[A: EncodeJson]: EntityEncoder[A] = jsonEncoderOf[A]
  }


  object NaiveArgonautCodec extends NaiveArgonautCodec

