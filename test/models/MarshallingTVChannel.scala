package models

import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID

class MarshallingTVChannel extends PlaySpec with MustMatchers {
  val id = BSONObjectID.generate
  val idString = id.stringify

  "Write and reads" should {
    "transform TVChannel object to json" in {
      Json.toJson(TVChannel("bbc1", "en", Some(id))).toString() mustBe s"""{"name":"bbc1","language":"en","id":\"$idString\"}"""
    }

    "transform json to TVChannel object" in {
      Json.parse(s"""{"name":"bbc1","language":"en","id":\"$idString\"}""").as[TVChannel] mustBe TVChannel("bbc1", "en", Some(id))
    }
  }
}