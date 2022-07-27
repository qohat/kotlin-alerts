package io.github.qohat.repo

import arrow.core.Either
import io.github.qohat.SlackUserIdAlreadyExists
import io.github.qohat.Unexpected
import io.github.qohat.UserError
import iogithubqohat.UsersQueries
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import java.util.*

@JvmInline
value class UserId(val id: UUID)
@JvmInline
value class SlackUserId(val id: String)
interface UserRepo {
    suspend fun insert(slackUserId: String, slackChannel: String): Either<UserError, UserId>
}

fun userRepo(queries: UsersQueries) = object: UserRepo {
    override suspend fun insert(slackUserId: String, slackChannel: String): Either<UserError, UserId> =
        Either.catch {
            queries
            .insertAndGetId(UserId(UUID.randomUUID()), SlackUserId(slackUserId), slackChannel)
            .executeAsOne()
        }.mapLeft { error ->
            if (error is PSQLException && error.sqlState == PSQLState.UNIQUE_VIOLATION.state) {
                SlackUserIdAlreadyExists(slackUserId)
            } else {
                Unexpected("Failed to persist user", error)
            }
        }
}