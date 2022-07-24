package io.github.qohat.repo

import arrow.core.Either
import io.github.qohat.UserError
import java.util.*

interface SubscriptionRepo {
    suspend fun insert(userId: String, repositoryId: String): Either<UserError, UserId>
}