package com.c12e.repos

package object spark {

  def notNull[T](name: String, value: T): Boolean =  Option(value).isDefined
  //require(Option(value).isDefined, s"$name cannot be null")



}


