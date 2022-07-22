package io.github.qohat.repo

import arrow.core.Either
import io.github.qohat.UserError

@JvmInline
value class UserId(val serial: Long)

interface UserRepo {
    suspend fun insert(username: String, email: String, password: String): Either<UserError, UserId>
}