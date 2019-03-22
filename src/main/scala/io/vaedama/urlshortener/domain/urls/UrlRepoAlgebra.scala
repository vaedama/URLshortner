package io.vaedama.urlshortener
package domain.urls

trait UrlRepoAlgebra[F[_]] {

  type ShortUrl = String

  def create(url: Url): F[Url]

  def get(shortUrl: ShortUrl): F[Option[Url]]

  def update(url: Url): F[Url]

  def delete(shortUrl: ShortUrl): F[Option[Url]]

}
