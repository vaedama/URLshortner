package io.vaedama.urlshortener
package domain.urls

import java.util.UUID

case class Url(
  originalUrl: String,
  shortUrl: Option[String],
  creator: UUID,
  expiry: Option[Long] = None,
  createdOn: Long = System.currentTimeMillis)
