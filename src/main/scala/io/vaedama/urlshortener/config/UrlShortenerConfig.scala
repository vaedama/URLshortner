package io.vaedama.urlshortener
package config

final case class ServerConfig(host: String, port: Int)

final case class UrlShortenerConfig(server: ServerConfig)