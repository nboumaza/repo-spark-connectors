package com.c12e.repos

/**
  * package utility
  * Created by nboumaza on 12/2/16.
  */
package object spark {

  def notNull[T](name: String, value: T): Boolean =  Option(value).isDefined

  //require(Option(value).isDefined, s"$name cannot be null")

}
