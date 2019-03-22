package io.vaedama.urlshortener
package domain.urls

import cats.Monad
import cats.data.EitherT
import cats.implicits._

class UrlService[F[_] : Monad](repo: UrlRepoAlgebra[F], validation: UrlValidationAlgebra[F], shortener: UrlShortener[F]) {

  def createUrl(url: Url): EitherT[F, UrlAlreadyExistsError, Url] = {
    url.shortUrl match {
      case Some(shortUrl) => validation.doesNotExist(shortUrl).flatMap(_ => EitherT.liftF(createWithShortUrl(url)))
      case None => EitherT.liftF(createWithShortUrl(url))
    }
  }

  private def createWithShortUrl(url: Url): F[Url] = {
    val shortUrl = url.shortUrl.orElse(Some(shortener.shorten(url.originalUrl)))
    val withShortUrl = url.copy(shortUrl = shortUrl)
    repo.create(withShortUrl)
  }

  def getUrl(shortUrl: String): EitherT[F, UrlNotFoundError.type, Url] = {
    EitherT.fromOptionF(repo.get(shortUrl), UrlNotFoundError)
  }

  def updateUrl(url: Url): EitherT[F, UrlNotFoundError.type, Url] = {
    url.shortUrl match {
      case Some(shortUrl) => validation.exists(shortUrl).flatMap(_ => EitherT.liftF(repo.update(url)))
      case None => EitherT.liftF(repo.update(url))
    }
  }

  def deleteUrl(shortUrl: String): F[Unit] = {
    repo.delete(shortUrl).as(())
  }

}

object UrlService {

  def apply[F[_] : Monad](repo: UrlRepoAlgebra[F], validation: UrlValidationAlgebra[F], shortener: UrlShortener[F]): UrlService[F] =
    new UrlService(repo, validation, shortener)

}
