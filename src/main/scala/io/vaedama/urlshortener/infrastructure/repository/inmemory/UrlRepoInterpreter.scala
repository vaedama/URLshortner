package io.vaedama.urlshortener
package infrastructure.repository.inmemory

import cats.Applicative
import cats.implicits._
import io.vaedama.urlshortener.domain.urls.{Url, UrlRepoAlgebra}

import scala.collection.concurrent.TrieMap

class UrlRepoInterpreter[F[_] : Applicative] extends UrlRepoAlgebra[F] {

  private val cache = new TrieMap[ShortUrl, Url]

  override def create(url: Url): F[Url] = {
    url.shortUrl.foreach(key => cache += key -> url)
    url.pure[F]
  }

  override def get(shortUrl: String): F[Option[Url]] = {
    cache.get(shortUrl).pure[F]
  }

  override def update(url: Url): F[Url] = {
    url.shortUrl.foreach(key => cache.update(key, url))
    url.pure[F]
  }

  override def delete(shortUrl: String): F[Option[Url]] = {
    cache.remove(shortUrl).pure[F]
  }

}
