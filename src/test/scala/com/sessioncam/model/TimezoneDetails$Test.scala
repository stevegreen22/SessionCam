package com.sessioncam.model

import com.sessioncam.CustomUnitSpec
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, Matchers}

/**
  * Created by SteveGreen on 31/01/2016.
  */
@RunWith(classOf[JUnitRunner])
class TimezoneDetails$Test extends CustomUnitSpec with Matchers with BeforeAndAfterEach {

  val NAME = "some-name"
  val TIMEZONE = "some-timezone"
  val DATETIME = new DateTime("2016-01-30T10:51:23.697Z")

  behavior of "Case Class creation for TimezoneDetails"

  it should "create an object of that type" in {
    val testObj = TimezoneDetails(
      NAME, TIMEZONE, DATETIME)

    inside(testObj) {
      case TimezoneDetails(name, timezone, jodadate) =>
        name shouldBe "some-name"
        timezone shouldBe "some-timezone"
        jodadate shouldBe new DateTime("2016-01-30T10:51:23.697Z")
    }
  }
}