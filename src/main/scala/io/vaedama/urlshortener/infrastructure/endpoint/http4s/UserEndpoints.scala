package io.vaedama.urlshortener
package infrastructure.endpoint.http4s

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import domain.users.{SignupRequest, User, UserAlreadyExistsError, UserService}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}
import tsec.passwordhashers.PasswordHasher

import scala.language.higherKinds

class UserEndpoints[F[_] : Effect, A] extends Http4sDsl[F] {

  implicit val userDecoder: EntityDecoder[F, User] = jsonOf
  implicit val signupReqDecoder: EntityDecoder[F, SignupRequest] = jsonOf

  private def signupEndpoint(userService: UserService[F], crypt: PasswordHasher[F, A]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req@POST -> Root / "users" =>
        val action = for {
          signup <- req.as[SignupRequest]
          hash   <- crypt.hashpw(signup.password)
          user   <- signup.asUser(hash).pure[F]
          result <- userService.createUser(user).value
        } yield result

        action.flatMap {
          case Right(saved) =>
            Ok(saved.asJson)
          case Left(UserAlreadyExistsError(existing)) =>
            Conflict(s"User with email: ${existing.email} already exists")
        }
    }

  def endpoints(userService: UserService[F], cryptService: PasswordHasher[F, A]): HttpRoutes[F] =
    signupEndpoint(userService, cryptService)
}

object UserEndpoints {

  def endpoints[F[_] : Effect, A](userService: UserService[F], cryptService: PasswordHasher[F, A]): HttpRoutes[F] =
    new UserEndpoints[F, A].endpoints(userService, cryptService)

}
