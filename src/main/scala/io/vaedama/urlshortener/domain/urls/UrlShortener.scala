package io.vaedama.urlshortener.domain.urls

trait UrlShortener[F[_]] {

  /**
    *
    * @param originalUrl
    * @return short link/alias of the original URL
    */
  def shorten(originalUrl: String): String

}
