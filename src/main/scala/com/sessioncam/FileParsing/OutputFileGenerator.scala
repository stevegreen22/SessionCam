package com.sessioncam.FileParsing

import java.io._
import java.nio.file.{FileAlreadyExistsException, Paths, Files}

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by SteveGreen on 30/01/2016.
  *
  * Purpose of the class is to create a file in a location (specified or not) and write the contents to it.
  */
case class OutputFileGenerator(saveFileLocation: String = "/Users/steveGreen/Development/Dev Workspace/SessionCam/dataOutput",
                          var fileName: String = "JsonOutputFile",
                          fileExtension: String = ".json",
                          fileContents: String) extends LazyLogging{

  object OutputFileGenerator {

    private var completeFilename = saveFileLocation + "/" + fileName + fileExtension
    private var version = 1

    //Version 4
    def createOutputFile(): Unit = {
      try {
        if (doesFileExist(completeFilename)) {
          createNewFilename
        }
        logger.info(s"Writing contents to file (${fileName}) with extension ${fileExtension} to: ${saveFileLocation}")
        val writer = new PrintWriter(new File(completeFilename))
        writer.write(fileContents)
        writer.close()
        logger.info(s"The file ${completeFilename} has been successfully created.")
      } catch {
        case fnf: FileNotFoundException => logger.error("Exception when writing file : " + fnf.getMessage)
        case fae: FileAlreadyExistsException => logger.error(s"Exception: The File ${completeFilename} already exists.")
        case _: Exception => logger.error("Unexpected Exception when writing file: ")
      }
    }

    private def doesFileExist(completeFilename: String) : Boolean = {
      logger.info("Checking if file already exists")
      Files.exists(Paths.get(completeFilename))
    }

    private def createNewFilename: Unit = {

      val RegexPattern = "(.+(?=\\/))(\\/)(.+(?=\\.))(.*)".r
      val RegexPattern(dir, delimiter, file, ext) = completeFilename

      val RegexUpdated = "([a-zA-Z0-9]+)([(\\d)]+)".r
      val updatedFilename = RegexUpdated
        file match {
          case updatedFilename(_*) =>
            //shit!  will need to cache the incoming filename to make this in any way useful.
            //this will only make sense if we generate multiple files within the same instance
            //val updateVersion = file.substring(file.lastIndexOf("(")+1, file.length-1)
            //version = updateVersion.toInt + 1
            version += 1
            fileName = s"$fileName($version)" //<- loving this.
          case _ => fileName = s"$fileName($version)"
      }
      //setter method for completeFilename?
      completeFilename = saveFileLocation + "/" + fileName + fileExtension
      logger.info(s"Creating a new filename and saving as ${completeFilename}")
    }
  }
}
