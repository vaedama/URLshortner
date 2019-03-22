package io.vaedama.urlshortener
package domain.users

import java.util.UUID

import tsec.passwordhashers.PasswordHash

case class User(
  firstName: String,
  lastName: String,
  email: String,
  passwordHash: String,
  phone: Option[String] = None,
  id: UUID = UUID.randomUUID,
  joinDate: Long = System.currentTimeMillis,
  lastUpdated: Long = System.currentTimeMillis)

case class SignupRequest(
  firstName: String,
  lastName: String,
  email: String,
  password: String,
  phone: Option[String]) {

  def asUser[A](hashedPassword: PasswordHash[A]): User = User(
    firstName,
    lastName,
    email,
    hashedPassword.toString,
    phone
  )

}
