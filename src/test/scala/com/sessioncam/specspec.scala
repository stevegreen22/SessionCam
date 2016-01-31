package com.sessioncam

import java.util.Date

import org.joda.time.DateTime
import org.json4s.native.Serialization
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import java.text.SimpleDateFormat

import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class WorkspaceSpec extends FlatSpec with CustomJsonFormats {
  it should "do" in {
    val d1 = new DateTime()
    val d1Json: String = Serialization.write(d1)
    println("json = " + d1Json)
    val d2 = Serialization.read[DateTime](d1Json)
    println(d1)
    println(d2)
  }

  it should "parse date" in {
    val format: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val parse: Date = format.parse("2014-02-28T23:29:39.290Z")
    println(parse)
  }
}