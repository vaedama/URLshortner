package io.vaedama.urlshortener
package domain.urls

import cats.data.EitherT

trait UrlValidationAlgebra[F[_]] {

  def exists(shortUrl: String): EitherT[F, UrlNotFoundError.type, Unit]

  def doesNotExist(shortUrl: String): EitherT[F, UrlAlreadyExistsError, Unit]

}
