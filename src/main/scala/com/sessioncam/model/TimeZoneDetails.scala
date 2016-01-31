package com.sessioncam.model

import org.joda.time.DateTime

/**
  * Created by SteveGreen on 29/01/2016.
  *
  * A simple model to represent the Timezone details
  */
case class TimezoneDetails(var name: String, var timezone: String, var jodaDate: DateTime) {
  def apply(s: String, s1: String, time: DateTime): TimezoneDetails = {
    new TimezoneDetails(name = s, timezone = s1, jodaDate = time)
  }
}
