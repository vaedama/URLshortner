package io.vaedama.urlshortener
package domain.urls

import cats.Monad
import cats.data.EitherT
import cats.implicits._

class UrlValidationInterpreter[F[_] : Monad](urlRepo: UrlRepoAlgebra[F]) extends UrlValidationAlgebra[F] {

  override def doesNotExist(shortUrl: String): EitherT[F, UrlAlreadyExistsError, Unit] = EitherT {
    urlRepo.get(shortUrl).map {
      case Some(url) => Left(UrlAlreadyExistsError(url))
      case None => Right(())
    }
  }

  override def exists(shortUrl: String): EitherT[F, UrlNotFoundError.type, Unit] = EitherT {
    urlRepo.get(shortUrl).map {
      case None => Left(UrlNotFoundError)
      case Some(_) => Right(())
    }
  }

}

sealed trait UrlValidationError extends Product with Serializable

case object UrlNotFoundError extends UrlValidationError

case class UrlAlreadyExistsError(url: Url) extends UrlValidationError
