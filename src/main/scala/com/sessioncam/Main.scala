package com.sessioncam

import java.io.FileNotFoundException

import com.sessioncam.CustomException.TimezoneNotSupportedException
import com.sessioncam.FileParsing.{InputFileParser, OutputFileGenerator}
import com.sessioncam.customfilters.CustomJsonFilters.createTimezoneFilteredList
import com.sessioncam.jsonparsing.conversion.DateConvertor
import com.sessioncam.jsonparsing.deserialisation.JsonDeserialiser
import com.sessioncam.jsonparsing.serialisation.JsonSerialiser._
import com.sessioncam.model.TimezoneDetails
import com.typesafe.scalalogging.LazyLogging
/**
  * Created by SteveGreen on 27/01/2016.
  */
object Main extends LazyLogging {

  private var listOfTimezones = List[TimezoneDetails]()
  private val FILTER_TIMEZONE = "Etc/UTC" //UTC -> ETC => +5 // UTC == GMT
  private val TO_TIMEZONE = "Etc/GMT-5" //GMT+0500
  private val DEFAULT_INPUT_LOCATION = "/Users/steveGreen/Development/Dev Workspace/SessionCam/dataInput"

//  @InjectMocks grr, these should be injected. - look at MacWire in more detail
  private lazy val FILE_READER_INSTANCE = new InputFileParser
  private lazy val DATE_CONVERTER_INSTANCE = new DateConvertor
  private lazy val JSON_DESERIALISE_INSTANCE = new JsonDeserialiser

  def main(args: Array[String]) {
    try{
      val files = FILE_READER_INSTANCE.getListOfFilesFromDirectory(DEFAULT_INPUT_LOCATION, List("json"))
      if (files.nonEmpty) {
        listOfTimezones = JSON_DESERIALISE_INSTANCE.createTimezoneListFromJsonFile(files)

        //Todo: allow an option of removing the filter from the list so that all timezones are updated
        logger.info(s"Attempting to filter by $FILTER_TIMEZONE")
        val utcTimezoneDetails = createTimezoneFilteredList(listOfTimezones, FILTER_TIMEZONE)

        try {
          logger.info(s"Converting the timezones to $TO_TIMEZONE")
          //utcTimezoneDetails.foreach(println) //before conversion
          for (timezone <- utcTimezoneDetails) {
            timezone.jodaDate = DATE_CONVERTER_INSTANCE.convertTimezone(timezone, TO_TIMEZONE)
          }
          //utcTimezoneDetails.foreach(println) //after
          val json = createJsonFromTimezoneList(utcTimezoneDetails)
          val outputFileGenerator = new OutputFileGenerator(fileContents = json)
          outputFileGenerator.OutputFileGenerator.createOutputFile() //todo: <- can this be cleaned up?

        } catch {
          case tns : TimezoneNotSupportedException => logger.error(s"Exception: The timezone $TO_TIMEZONE is not supported")
        }

      } else {
        //todo:handle empty filelist without an exception?
      }
    } catch {
      case iae : IllegalArgumentException => logger.error("Exception: " + iae.getMessage)
      case npe : NullPointerException => logger.error("Null Pointer Exception: " + npe.getMessage)
      case fnf : FileNotFoundException => logger.error("Exception when searching for files: " +fnf.getMessage)
      case _ : Exception => logger.error("Unknown Exception")
    }
  }


}
//this has grouped the timezoneObjects by timezone value.
//println(listOfTimezones.groupBy(_.timezone).mapValues(_.map(_.copy())))


//Done: Update a list each time a json object from either of the files is read in.
//Done: Use this complete list to create an aggregated collection
//Done: Pass the items of the list through the time converter to adjust the time
//Done: This updated list can now be parsed into an output JSON file
//Done: Update main method so jar can be run from terminal
//Todo: Accept args from command line
//Todo: TESTS!!!! - getting there...
//Done: save a list of the timezones found in the json, run them all through the converter changing as neccesary
//Done: whitelist of accepted cannonical timezone to test against
//todo: Filtering the lsit first removes some of the functionality - either rework it to do both or allow the option via args
