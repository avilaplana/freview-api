package controllers

import controllers.external.{ProgramLong, TVContentLong}
import models._
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat
import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID

class MarshallingProgramSpec extends PlaySpec with MustMatchers {
  val id = BSONObjectID.generate
  val idString = id.stringify
  val now = new DateTime(2014, 10, 10, 10, 0, 0, DateTimeZone.forID("UTC"))
  val fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")

  val program = TVContentLong(
    channel = "bbc1",
    channelImageURL = "http://localhost:9000/bbc1.png",
    provider = List("FREEVIEW", "SKY"),
    start = now,
    end = now.plusHours(2),
    rating = None,
    program = Some(ProgramLong(
      title = "program3",
      plot = Some("plot3"))),
    series = None,
    film = None,
    onTimeNow = false,
    minutesLeft = Some(120))

  "Write and reads" should {
    "transform TVContent Long 'program' object to json" in {

      val tvContentJson = Json.toJson(program)

      (tvContentJson \ "channel").as[String] mustBe "bbc1"
      (tvContentJson \ "channelImageURL").as[String] mustBe "http://localhost:9000/bbc1.png"
      (tvContentJson \ "provider").as[List[String]] mustBe Seq("FREEVIEW", "SKY")
      (tvContentJson \ "start").as[String] mustBe s"${fmt.print(now.withZone(DateTimeZone.forID("Europe/London")))}"
      (tvContentJson \ "end").as[String] mustBe s"${fmt.print(now.plusHours(2).withZone(DateTimeZone.forID("Europe/London")))}"
      (tvContentJson \ "program" \ "title").as[String] mustBe "program3"
      (tvContentJson \ "program" \ "plot").as[String] mustBe "plot3"
      (tvContentJson \ "onTimeNow").as[Boolean] mustBe false
      (tvContentJson \ "minutesLeft").as[Double] mustBe 120
    }

    "transform program json to TVContent Long 'program' object" in {
      val programJson = s"""{"channel":"bbc1",
        |"channelImageURL":"http://localhost:9000/bbc1.png",
        |"provider":["FREEVIEW","SKY"],
        |"start":"${fmt.print(now.withZone(DateTimeZone.forID("Europe/London")))}",
        |"end":"${fmt.print(now.plusHours(2).withZone(DateTimeZone.forID("Europe/London")))}",
        |"rating": null,
        |"program":{
        | "title":"program3",
        | "plot":"plot3"},
        |"series":null,
        |"film":null,
        |"onTimeNow":false,
        |"minutesLeft":120}""".stripMargin

      Json.parse(programJson).as[TVContentLong] mustBe program.copy(
        start = now.withZone(DateTimeZone.forID("Europe/London")),
        end = now.plusHours(2).withZone(DateTimeZone.forID("Europe/London")))
    }
  }
}