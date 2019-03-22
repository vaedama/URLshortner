package io.vaedama.urlshortener
package domain.users

import java.util.UUID

import cats.data.EitherT

trait UserValidationAlgebra[F[_]] {

  def doesNotExist(user: User): EitherT[F, UserAlreadyExistsError, Unit]

  def exists(userID: UUID): EitherT[F, UserNotFoundError.type, Unit]

}
