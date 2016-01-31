package com.sessioncam.FileParsing

import java.io.{File, FileNotFoundException}

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by SteveGreen on 27/01/2016.
  */
class InputFileParser extends LazyLogging {

/**
  * Function to take in a directory name as string and return a list
  * of files within that directory also checking that their extension
  * matches those given
  *
  * @param directory  : the directory that we wish to search for files
  * @param extensions : a list of strings representing the extensions we are looking for
  * @return A List containing the files
  */
  @throws(classOf[FileNotFoundException])
  def getListOfFilesFromDirectory(directory: String, extensions: List[String]): List[File] = {
    logger.info("Attempting to read files from specified directory")
    var resultList = List[File]()
    val dir = new File(directory)
    try {
      if (dir.exists && dir.isDirectory) {
        resultList = dir.listFiles.filter(_.isFile).toList.filter { file =>
          extensions.exists(file.getName.endsWith)
        }
      } else {
        throw new FileNotFoundException("No files were found in that location")
      }
    }
    resultList
  }

}

