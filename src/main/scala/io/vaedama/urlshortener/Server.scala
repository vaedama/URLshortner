package io.vaedama.urlshortener

import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.config.parser
import config._
import domain.users.{UserService, UserValidationInterpreter}
import infrastructure.endpoint.http4s.UserEndpoints
import infrastructure.repository.inmemory.UserRepoInterpreter
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{Router, Server => H4Server}
import org.slf4j.LoggerFactory
import tsec.passwordhashers.jca.BCrypt

object Server extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    createServer.use(_ => IO.never).as(ExitCode.Success)
  }

  private def createServer[F[_] : ContextShift : ConcurrentEffect : Timer]: Resource[F, H4Server[F]] = {
    for {
      conf          <- Resource.liftF(parser.decodePathF[F, UrlShortenerConfig]("urlshortener"))
      logger        <- Resource.liftF(Slf4jLogger.fromSlf4j[F](LoggerFactory.getLogger(this.getClass)))
      userRepository = UserRepoInterpreter[F]
      userValidation = UserValidationInterpreter[F](userRepository)
      userService    = UserService[F](userRepository, userValidation)
      services       = UserEndpoints.endpoints[F, BCrypt](userService, BCrypt.syncPasswordHasher[F])
      httpApp        = Router("/" -> services).orNotFound
      server        <- BlazeServerBuilder[F].bindHttp(conf.server.port, conf.server.host).withHttpApp(httpApp).resource
    } yield server
  }

}
