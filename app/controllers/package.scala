import models._
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson._

import scala.util.Try

package object controllers {

  implicit object BSONObjectIDFormat extends Format[BSONObjectID] {
    def writes(objectId: BSONObjectID): JsValue = JsString(objectId.stringify)

    def reads(json: JsValue): JsResult[BSONObjectID] = json match {
      case JsString(x) => {
        val maybeOID: Try[BSONObjectID] = BSONObjectID.parse(x)
        if (maybeOID.isSuccess) JsSuccess(maybeOID.get)
        else {
          JsError("Expected BSONObjectID as JsString")
        }
      }
      case _ => JsError("Expected BSONObjectID as JsString")
    }
  }

  implicit val reviewReads: Reads[TVChannel] = (
    (__ \ "name").read[String] and
      (__ \ "provider").read[List[String]] and
      (__ \ "id").read[Option[BSONObjectID]]
    )(TVChannel.apply _)

  implicit val tvChannelWrites = new Writes[TVChannel] {
    override def writes(tvchannel: TVChannel): JsValue = Json.obj(
      "name" -> tvchannel.name,
      "provider" -> tvchannel.provider,
      "id" -> tvchannel.id,
      "uriToday" -> tvchannel.uriToday,
      "uriCurrent" -> tvchannel.uriCurrent,
      "uriLeft" -> tvchannel.uriLeft
    )
  }

  implicit val programFmt = Json.format[Program]
  implicit val serieFmt = Json.format[Serie]
  val pattern = "yyyy-MM-dd'T'HH:mm:ss"

  implicit val dateFormat = Format[DateTime](
    Reads.jodaDateReads(pattern),
    Writes.jodaDateWrites(pattern))

  implicit val programShortFmt = Json.format[ProgramShort]
  implicit val serieShortFmt = Json.format[SerieShort]

  implicit val tvProgramReads: Reads[TVProgram] = ((__ \ "channel").read[String] and
      (__ \ "start").read[DateTime].map[DateTime]{ dt => dt.withZoneRetainFields(DateTimeZone.forID("Europe/London"))} and
      (__ \ "end").read[DateTime].map[DateTime](dt => dt.withZoneRetainFields(DateTimeZone.forID("Europe/London"))) and
      (__ \ "category").read[Option[List[String]]] and
      (__ \ "accessibility").read[Option[List[String]]] and
      (__ \ "series").read[Option[Serie]] and
      (__ \ "program").read[Option[Program]] and
      (__ \ "id").read[Option[BSONObjectID]]
      )(TVProgram.apply _)

  implicit val tvProgramWrites = new Writes[TVProgram] {
    override def writes(tvprogram: TVProgram): JsValue = Json.obj(
      "channel" -> tvprogram.channel,
      "start" -> tvprogram.start.toDateTime(DateTimeZone.forID("Europe/London")),
      "end" -> tvprogram.end.toDateTime(DateTimeZone.forID("Europe/London")),
      "category" -> tvprogram.category,
      "accessibility" -> tvprogram.accessibility,
      "series" -> tvprogram.series,
      "program" -> tvprogram.program,
      "id" -> tvprogram.id
    )
  }

  implicit val tvProgramShortReads: Reads[TVProgramShort] = (
    (__ \ "channel").read[String] and
      (__ \ "start").read[DateTime].map[DateTime](dt => dt.withZoneRetainFields(DateTimeZone.forID("Europe/London"))) and
      (__ \ "end").read[DateTime].map[DateTime](dt => dt.withZoneRetainFields(DateTimeZone.forID("Europe/London"))) and
      (__ \ "category").read[Option[List[String]]] and
      (__ \ "series").read[Option[SerieShort]] and
      (__ \ "program").read[Option[ProgramShort]] and
      (__ \ "id").read[Option[BSONObjectID]]
    )(TVProgramShort.apply _)


  implicit val tvProgramShortWrites = new Writes[TVProgramShort] {
    override def writes(tvprogram: TVProgramShort): JsValue = Json.obj(
      "channel" -> tvprogram.channel,
      "start" -> tvprogram.startTime.toDateTime(DateTimeZone.forID("Europe/London")),
      "end" -> tvprogram.endTime.toDateTime(DateTimeZone.forID("Europe/London")),
      "category" -> tvprogram.category,
      "series" -> tvprogram.series,
      "program" -> tvprogram.program,
      "uriTVProgramDetails" -> tvprogram.uriTVProgramDetails,
      "id" -> tvprogram.id
    )
  }


  implicit val tvChannelGenreFmt = Json.format[TVChannelGenre]
  implicit val tvContentGenreFmt = Json.format[TVContentGenre]
}
