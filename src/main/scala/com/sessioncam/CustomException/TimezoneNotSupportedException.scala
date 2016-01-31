package com.sessioncam.CustomException

/**
  * Created by SteveGreen on 31/01/2016.
  */
class TimezoneNotSupportedException private(ex: RuntimeException) extends RuntimeException(ex) {
  def this(message:String) = this(new RuntimeException(message))
  def this(message:String, throwable: Throwable) = this(new RuntimeException(message, throwable))
}

object TimezoneNotSupportedException {
  def apply(message:String) = new TimezoneNotSupportedException(message)
  def apply(message:String, throwable: Throwable) = new TimezoneNotSupportedException(message, throwable)
}

