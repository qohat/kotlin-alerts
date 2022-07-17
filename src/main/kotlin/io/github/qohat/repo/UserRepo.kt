package io.github.qohat.repo

import arrow.core.Either
import at.favre.lib.crypto.bcrypt.BCrypt
import io.github.qohat.Unexpected
import io.github.qohat.UserError
import io.github.qohat.UsernameAlreadyExists
import iogithubqohat.UsersQueries
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState

@JvmInline
value class UserId(val serial: Long)

interface UserRepo {
    suspend fun insert(username: String, email: String, password: String): Either<UserError, UserId>
}

fun userPersistence(usersQueries: UsersQueries, rounds: Int = 10) = object : UserRepo {
    override suspend fun insert(username: String, email: String, password: String): Either<UserError, UserId> =
        Either.catch {
            val hash = BCrypt.withDefaults().hash(rounds, password.toByteArray())
            usersQueries.insertAndGetId(email, username, hash, "", "").executeAsOne()
        }.mapLeft { e: Throwable ->
            if (e is PSQLException && e.sqlState == PSQLState.UNIQUE_VIOLATION.state) {
                UsernameAlreadyExists(username)
            } else {
                Unexpected("Failed inserting user record $username:$email", e)
            }
        }
}