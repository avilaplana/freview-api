package controllers

import models.{ChannelGenreRepository, TVChannelGenreRepository}
import play.api.libs.json.Json
import play.api.mvc.Action
import scala.concurrent.ExecutionContext.Implicits.global

object TVChannelGenreController extends TVChannelGenreController {
  override val channelGenreReporitory = new TVChannelGenreRepository("tvChannelGenre")
}

trait TVChannelGenreController extends BaseController {
  val channelGenreReporitory: ChannelGenreRepository

  def genres() = Action.async {
    channelGenreReporitory.findAll().map{
      case head :: tail => {
        Ok(Json.toJson(head :: tail))
      }
      case Nil => {
        NotFound
      }
    }
  }
}