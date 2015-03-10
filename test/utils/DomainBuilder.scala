package utils

import controllers._
import models._
import org.joda.time.DateTimeZone

object DomainBuilder {

  object TVShortWithTimeZone {
    def apply(tvContent: TVContent): TVContentShort = {
      TVShort(tvContent.copy(start = tvContent.start.withZone(DateTimeZone.forID("Europe/London")),
        end = tvContent.end.withZone(DateTimeZone.forID("Europe/London"))))
    }
  }

  object TVLongWithTimeZone {
    def apply(tvProgram: TVContent): TVContentLong = TVLong(tvProgram.copy(
      start = tvProgram.start.withZone(DateTimeZone.forID("Europe/London")),
      end = tvProgram.end.withZone(DateTimeZone.forID("Europe/London"))))
  }

}
