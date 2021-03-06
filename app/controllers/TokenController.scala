package controllers

import gateway.AuthGateway
import infrastructure.{Decryptor, InternalServerErrorDownstreamException, BadRequestDownstreamException}
import models.Authorization
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TokenController extends Controller {


  val authGateway: AuthGateway
  val encoder: Decryptor

  def token = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[Authorization].fold(
      errors => Future.successful(BadRequest),
      authIn => {
        authGateway.createToken(authIn).map {
          a => Created(
            s"""{"token":"${encoder.encode(a.username, a.token)}"}"""
          )
        } recover {
          case e@BadRequestDownstreamException => BadRequest
          case e@InternalServerErrorDownstreamException => InternalServerError
          case _ => InternalServerError
        }
      }
    )
  }
}

object TokenController extends TokenController {
  override val authGateway: AuthGateway = AuthGateway
  override val encoder: Decryptor = Decryptor
}
