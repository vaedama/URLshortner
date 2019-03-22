package io.vaedama.urlshortener
package domain.users

import java.util.UUID

trait UserRepoAlgebra[F[_]] {

  def create(user: User): F[User]

  def get(userID: UUID): F[Option[User]]

  def update(user: User): F[User]

  def delete(userID: UUID): F[Option[User]]

  def findByEmail(email: String): F[Option[User]]

}
