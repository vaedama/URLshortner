package io.vaedama.urlshortener
package infrastructure.repository.inmemory

import java.util.UUID

import cats.Applicative
import cats.implicits._
import domain.users.{User, UserRepoAlgebra}

import scala.collection.concurrent.TrieMap

class UserRepoInterpreter[F[_] : Applicative] extends UserRepoAlgebra[F] {

  private val cache = new TrieMap[UUID, User]

  override def create(user: User): F[User] = {
    cache += user.id -> user
    user.pure[F]
  }

  override def get(userID: UUID): F[Option[User]] = {
    cache.get(userID).pure[F]
  }

  override def update(user: User): F[User] = {
    cache.update(user.id, user)
    user.pure[F]
  }

  override def delete(userID: UUID): F[Option[User]] = {
    cache.remove(userID).pure[F]
  }

  override def findByEmail(email: String): F[Option[User]] = {
    cache.values.find(_.email == email).pure[F]
  }

}

object UserRepoInterpreter {
  def apply[F[_]: Applicative]() = new UserRepoInterpreter[F]
}