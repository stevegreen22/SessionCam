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

import scala.swing.BorderPanel.Position._
import scala.swing._
import scala.swing.event._

object SessionCamProject extends SimpleSwingApplication with LazyLogging{

  private var listOfTimezones = List[TimezoneDetails]()
  private val FILTER_TIMEZONE = "Etc/UTC" //UTC -> ETC => +5 // UTC == GMT
  private val TO_TIMEZONE = "Pacific/Marquesas"//Etc/GMT-5" //GMT+0500
  private val DEFAULT_INPUT_LOCATION = "/Users/steveGreen/Development/Dev Workspace/SessionCam/dataInput"

  private lazy val FILE_READER_INSTANCE = new InputFileParser
  private lazy val DATE_CONVERTER_INSTANCE = new DateConvertor
  private lazy val JSON_DESERIALISE_INSTANCE = new JsonDeserialiser
  private var utcTimezoneDetails = List[TimezoneDetails]()
  //Swing
  def top = new MainFrame {
    title = "SessionCam Project"

    val resetButton = new Button {
      text = "Reset"
    }

    val getListOfFilesButton = new Button {
      text = "Search for files!"
    }
    val createAFilteredListButton = new Button {
      text = "Create filtered list"
    }
    val runConversionButton = new Button {
      text = "Run Conversion"
    }
    val createOutputFilesButton = new Button {
      text = "Create output"
    }
    var inputLabel = new Label("Enter location of input folder = ")

    object defaultOutputLocation extends TextField {
      text = "/Users/steveGreen/Development/Dev Workspace/SessionCam/dataOutput"
      columns = 50
    }
    object defaultInputLocation extends TextField {
      text = DEFAULT_INPUT_LOCATION
      columns = 50
    }
    object defaultFilter extends TextField {
      text = FILTER_TIMEZONE
      columns = 5
    }
    object defaultConversion extends TextField {
      text = TO_TIMEZONE
      columns = 10
    }

    object filesListArea extends TextArea(rows = 5, columns = 25)
    val filesListAreaPane = new ScrollPane(filesListArea)

    object filteredTimezoneListArea extends TextArea(rows = 10, columns = 25)
    val timezoneListAreaPane = new ScrollPane(filteredTimezoneListArea)

    object convertedListArea extends TextArea(rows = 10, columns = 25)
    val convertedListAreaPane = new ScrollPane(convertedListArea)

    object outputFilesListArea extends TextArea(rows = 10, columns = 25)
    val outputFilesListAreaPane = new ScrollPane(outputFilesListArea)

    object filterTimezone extends TextField { columns = 5 }

    contents = new BorderPanel {
      layout(new BoxPanel(Orientation.Vertical) {
        contents += new Label("Input Location: ")
        contents += defaultInputLocation
        contents += filesListArea

        contents += new Label("Filter: ")
        contents += defaultFilter
        contents += new Label("")
        contents += filteredTimezoneListArea

        contents += new Label("Converted to TZ: ")
        contents += defaultConversion
        contents += convertedListArea

        contents += new Label("Output Location: ")
        contents += defaultOutputLocation
        contents += outputFilesListArea
      }) = East

      layout(new FlowPanel() {
        contents += getListOfFilesButton
        contents += createAFilteredListButton
        contents += runConversionButton
        contents += createOutputFilesButton
        contents += resetButton
      }) = South

      border = Swing.EmptyBorder(60, 20, 30, 20)
    }

    listenTo(getListOfFilesButton, createAFilteredListButton, runConversionButton)
    reactions += {
      case ButtonClicked(`getListOfFilesButton`) =>
        if (filesListArea.text.isEmpty) {
          val files = FILE_READER_INSTANCE.getListOfFilesFromDirectory(DEFAULT_INPUT_LOCATION, List("json"))
          if (files.nonEmpty) {
            filesListArea.text += "Files found in directory were: \n"
            for (file <- files) {
              filesListArea.text += file.getAbsoluteFile + "\n"
            }
            listOfTimezones = JSON_DESERIALISE_INSTANCE.createTimezoneListFromJsonFile(files)
            filesListArea.text += "Timezones found from files: \n"
            for (time <- listOfTimezones) {
              filesListArea.text += time.toString + "\n"
            }
          } else {
            filesListArea.text += "There were no files found. :("
          }
        }

      case ButtonClicked(`createAFilteredListButton`) =>
        utcTimezoneDetails = createTimezoneFilteredList(listOfTimezones, FILTER_TIMEZONE)
          filteredTimezoneListArea.text += "Filtering the list on " + FILTER_TIMEZONE + ": \n"
          for (timezone <- utcTimezoneDetails) {
            filteredTimezoneListArea.text += timezone.toString + "\n"
        }

      case ButtonClicked(`runConversionButton`) =>
        if (convertedListArea.text.isEmpty) {
          convertedListArea.text += "Converting the dates from " + FILTER_TIMEZONE + " to " + TO_TIMEZONE +": \n"

          for (timezone <- utcTimezoneDetails) {
            convertedListArea.text += "Previous: " + timezone.jodaDate + " - "
            timezone.jodaDate = DATE_CONVERTER_INSTANCE.convertTimezone(timezone, TO_TIMEZONE)
            convertedListArea.text += "Converted: " + timezone.jodaDate + "\n"
          }
        }

      case ButtonClicked(`createOutputFilesButton`) =>
        outputFilesListArea.text += "Creating output files: \n"
        val json = createJsonFromTimezoneList(utcTimezoneDetails)
        val outputFileGenerator = new OutputFileGenerator(fileContents = json)
        outputFileGenerator.OutputFileGenerator.createOutputFile()
        outputFilesListArea.text += "File has been saved" //grrr, pulling this in to here now is a pain.

    }

    centerOnScreen()
    open()
  }

}


object Main extends LazyLogging {

  private var listOfTimezones = List[TimezoneDetails]()
  private val FILTER_TIMEZONE = "Etc/UTC" //UTC -> ETC => +5 // UTC == GMT
  private val TO_TIMEZONE = "Etc/GMT-5" //GMT+0500
  private val DEFAULT_INPUT_LOCATION = "/Users/steveGreen/Development/Dev Workspace/SessionCam/dataInput"

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
          case tns : TimezoneNotSupportedException => logger.error(tns.getMessage)
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
