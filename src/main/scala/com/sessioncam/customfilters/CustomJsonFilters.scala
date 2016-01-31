package com.sessioncam.customfilters

import com.sessioncam.model.TimezoneDetails

/**
  * Created by SteveGreen on 30/01/2016.
  *
  * Could be built upon to filter on all manner of things...
  */
trait CustomJsonFiltering {

  /**
    * Method to filter out data that isn't required and return a new list.
    *
    * @param tzList the list of data to filter on
    * @param tz     the string that we are filtering for
    * @return a new list of TimezoneDetails dependant on the filter string
    */
  def createTimezoneFilteredList(tzList: List[TimezoneDetails], tz: String): List[TimezoneDetails] = tzList match {
    case Nil => tzList
    case x :: tzList => if (x.timezone.equalsIgnoreCase(tz)) x :: createTimezoneFilteredList(tzList, tz) else createTimezoneFilteredList(tzList, tz)
  }
}

object CustomJsonFilters extends CustomJsonFiltering{ }

/*
Todo: http://stackoverflow.com/questions/30040346/scala-unit-testing-stubbing-a-singleton
Look into this more.
 */

//TODO: IntelliJ recommending changing tzlist to `tzlist` as suspicious var which breaks the world - why
//todo: would be nice tolog this but not really wanting to wrap it in another method to do so...