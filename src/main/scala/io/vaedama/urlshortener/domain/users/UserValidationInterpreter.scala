package io.vaedama.urlshortener
package domain.users

import java.util.UUID

import cats.Monad
import cats.data.EitherT
import cats.implicits._

class UserValidationInterpreter[F[_] : Monad](userRepo: UserRepoAlgebra[F]) extends UserValidationAlgebra[F] {

  override def doesNotExist(user: User): EitherT[F, UserAlreadyExistsError, Unit] = EitherT {
    userRepo.findByEmail(user.email).map {
      case None => Right(())
      case Some(_) => Left(UserAlreadyExistsError(user))
    }
  }

  override def exists(userID: UUID): EitherT[F, UserNotFoundError.type, Unit] = EitherT {
    userRepo.get(userID).map {
      case None => Left(UserNotFoundError)
      case Some(_) => Right(())
    }
  }

}

object UserValidationInterpreter {

  def apply[F[_] : Monad](repo: UserRepoAlgebra[F]): UserValidationAlgebra[F] =
    new UserValidationInterpreter[F](repo)

}

sealed trait UserValidationError extends Product with Serializable

case object UserNotFoundError extends UserValidationError

case class UserAlreadyExistsError(user: User) extends UserValidationError
