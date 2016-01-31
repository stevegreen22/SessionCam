package com.sessioncam.jsonparsing.serialisation

import com.sessioncam.CustomJsonFormats
import com.sessioncam.model.TimezoneDetails
import com.typesafe.scalalogging.LazyLogging
import org.json4s.native.Serialization.write

/**
  * Created by SteveGreen on 30/01/2016.
  */
object JsonSerialiser extends LazyLogging with CustomJsonFormats {
  def createJsonFromTimezoneList(timezones: List[TimezoneDetails]) : String = {
    logger.info("Creating JSON from the data")
    write(timezones)
  }

}
