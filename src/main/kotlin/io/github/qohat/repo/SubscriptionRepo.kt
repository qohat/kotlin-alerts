package io.github.qohat.repo

import arrow.core.Either
import io.github.qohat.SubscriptionAlreadyExists
import io.github.qohat.SubscriptionError
import io.github.qohat.Unexpected
import io.github.qohat.UserError
import iogithubqohat.SubscriptionsQueries
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import java.time.OffsetDateTime


interface SubscriptionRepo {
    suspend fun insert(userId: UserId, repositoryId: RepoId, type: String, createdAt: OffsetDateTime): Either<SubscriptionError, Unit>
}

fun subscriptionRepo(queries: SubscriptionsQueries) = object : SubscriptionRepo {
    override suspend fun insert(userId: UserId, repositoryId: RepoId, type: String, createdAt: OffsetDateTime): Either<SubscriptionError, Unit> =
        Either.catch {
            queries.insert(userId, repositoryId, type, createdAt)
        }.mapLeft {
            error -> if (error is PSQLException && error.sqlState == PSQLState.UNIQUE_VIOLATION.state) {
                SubscriptionAlreadyExists(userId.id.toString(), repositoryId.id.toString())
            } else {
                Unexpected("Failed inserting subscription", error)
            }
        }
}