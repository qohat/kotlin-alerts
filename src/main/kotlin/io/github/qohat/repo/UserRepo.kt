package io.github.qohat.repo

import arrow.core.Either
import io.github.qohat.UserError
import iogithubqohat.UsersQueries
import java.util.*

@JvmInline
value class UserId(val id: UUID)
@JvmInline
value class SlackUserId(val id: UUID)
interface UserRepo {
    suspend fun insert(slackUserId: String, slackChannel: String): Either<UserError, UserId>
}

fun userRepo(queries: UsersQueries) = object: UserRepo {
    override suspend fun insert(slackUserId: String, slackChannel: String): Either<UserError, UserId> {
        TODO("Not yet implemented")
    }
}