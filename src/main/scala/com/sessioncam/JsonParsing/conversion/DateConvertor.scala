package com.sessioncam.jsonparsing.conversion

import java.util.TimeZone

import com.sessioncam.CustomException.TimezoneNotSupportedException
import com.sessioncam.model.TimezoneDetails
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{DateTime, DateTimeZone}

/**
  * Created by SteveGreen on 28/01/2016.
  *
  * Class will be used to convert DateTime objects.  Typical usage will
  * be converting from UTC to ECT
  *
  * Note:
  *  Coordinated Universal Time is 5 hours ahead of Ecuador Time
  *  12:13 Sunday, Coordinated Universal Time (UTC) is
  *  07:13 Sunday, Ecuador Time (ECT)
  *
  *  There is no time difference between Greenwich Mean Time and Coordinated Universal Time
  *  12:14 Sunday, Greenwich Mean Time (GMT) is
  *  12:14 Sunday, Coordinated Universal Time (UTC)
  *
  *  3 letter IDs are not ideal as many share the same, instead we use the
  *  canonincal id to help differentiate.
  *
  *  see @link{http://joda-time.sourceforge.net/timezones.html} for a complete list.
  *
  *  conversion set to +05:00	Etc/GMT-5
  */
class DateConvertor extends LazyLogging {

  private val whitelist = buildTimezoneWhitelist

  private def buildTimezoneWhitelist : List[String] = {
    io.Source.fromFile("/Users/SteveGreen/Development/Dev Workspace/SessionCam/src/main/TimezoneWhitelist").getLines.toList
  }


  /**
    * Convert method will attempt to convert from UTC to ECT and vice versa depending
    * on what the arguments of the class are.
    *
    * @return new modified DateTime object
    */
  def convertTimezone(timezoneDetails: TimezoneDetails, to: String) : DateTime = {

    if (to == "") {
      throw new IllegalArgumentException("Exception: Conversion 'to' must be set and not empty")
    } else if (!whitelist.contains(to)) {
      throw new TimezoneNotSupportedException(s"The timezone $to is not supported")
    } else {
      logger.info(s"Attempting to convert date ${timezoneDetails.jodaDate} from ${timezoneDetails.timezone} to ${to}")
      val currentTimezone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timezoneDetails.timezone))
      val conversionTimezone = DateTimeZone.forID(to)
      val convertedDate = timezoneDetails.jodaDate.withZone(conversionTimezone)
      logger.info(s"Date conversion successful:\n" +
        s"${timezoneDetails.jodaDate} with a timezone of ${timezoneDetails.timezone} to\n" +
        s"$convertedDate with a timezone of $to")
      convertedDate
    }
  }

}
