package io.vaedama.urlshortener
package domain.users

import java.util.UUID

import cats.Monad
import cats.data.EitherT
import cats.implicits._

class UserService[F[_] : Monad](repo: UserRepoAlgebra[F], validation: UserValidationAlgebra[F]) {

  def createUser(user: User): EitherT[F, UserAlreadyExistsError, User] = {
    validation.doesNotExist(user).flatMap(_ => EitherT.liftF(repo.create(user)))
  }

  def getUser(userID: UUID): EitherT[F, UserNotFoundError.type, User] = {
    EitherT.fromOptionF(repo.get(userID), UserNotFoundError)
  }

  def updateUser(user: User): EitherT[F, UserNotFoundError.type, User] = {
    validation.exists(user.id).flatMap(_ => EitherT.liftF(repo.update(user)))
  }

  def deleteUser(userID: UUID): F[Unit] = {
    repo.delete(userID).as(())
  }

}

object UserService {

  def apply[F[_] : Monad](repo: UserRepoAlgebra[F], validation: UserValidationAlgebra[F]): UserService[F] =
    new UserService[F](repo, validation)

}
