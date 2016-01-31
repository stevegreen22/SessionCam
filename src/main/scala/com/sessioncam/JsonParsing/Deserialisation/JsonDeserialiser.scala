package com.sessioncam.jsonparsing.deserialisation

import java.io.File

import com.sessioncam.CustomJsonFormats
import com.sessioncam.model.TimezoneDetails
import com.typesafe.scalalogging.LazyLogging
import org.json4s.native.JsonMethods._

/**
  * Created by SteveGreen on 29/01/2016.
  */
class JsonDeserialiser extends LazyLogging with CustomJsonFormats{

  /**
    * Method for extraction the JSON data from a given file
    * @param file : the file that we wish to get the json from
    * @return a string representing the contents of the JSON file
    */
  def getJsonContentsAsString(file : java.io.File): String = {
    logger.info(s"Attempting to get contents of file: ${file.getName} from directory: ${file.getCanonicalPath}")
    try {
      val fileDetails = scala.io.Source.fromFile(file)
      val str = fileDetails.mkString
      fileDetails.close
      str
    }
  }

  /**
    * Method for obtaining timezone details from a list of given files
    * @param files A List of files containing the data we're interested in
    * @return a List of TimezoneDetails objects taken from the json
    */
  def createTimezoneListFromJsonFile(files : List[File]): List[TimezoneDetails] = {
    logger.info("Creating list of timezones from JSON")
    var listOfTimezones = List[TimezoneDetails]()
    for (file <- files) {
      try {
        val str = getJsonContentsAsString(file)
        val timezones = parse(str) \ "data"
        val tzList = timezones.extract[List[TimezoneDetails]]
        listOfTimezones = listOfTimezones ::: tzList
      } catch {
        case e : Exception =>
          logger.error("There was a problem with creating the timezone list" +e.getMessage)
        case _ =>
          logger.error("There was a problem with creating the timezone list")
      }
    }
    listOfTimezones
  }



}
