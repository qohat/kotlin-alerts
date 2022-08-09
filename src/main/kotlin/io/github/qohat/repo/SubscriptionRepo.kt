package io.github.qohat.repo

import arrow.core.Either
import arrow.core.flatMap
import io.github.qohat.*
import io.github.qohat.codec.Codecs
import iogithubqohat.SubscriptionsQueries
import kotlinx.serialization.Serializable
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class UserSubscription(
    val organization: String,
    val repository: String,
    val userId: UserId,
    val subscribedAt: OffsetDateTime,
)
interface SubscriptionRepo {
    suspend fun insert(userId: UserId, repositoryId: RepoId, type: String, createdAt: OffsetDateTime): Either<SubscriptionError, Unit>
    suspend fun findBy(userId: UserId): List<UserSubscription>
    suspend fun findBy(repoId: RepoId): List<UserSubscription>
    suspend fun findBy(userId: UserId, repoId: RepoId): UserSubscription?
    suspend fun findAll(): List<UserSubscription>
    suspend fun delete(userId: UserId, repoId: RepoId)
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

    override suspend fun findBy(userId: UserId): List<UserSubscription> =
        queries.findByUser(userId, ::UserSubscription).executeAsList()

    override suspend fun findBy(repoId: RepoId): List<UserSubscription> =
        queries.findByRepo(repoId, ::UserSubscription).executeAsList()

    override suspend fun findBy(userId: UserId, repoId: RepoId): UserSubscription? =
        queries.findByUserAndRepo(userId, repoId, ::UserSubscription).executeAsOne()

    override suspend fun findAll(): List<UserSubscription> =
        queries.findAll(::UserSubscription).executeAsList()

    override suspend fun delete(userId: UserId, repoId: RepoId) =
        queries.delete(userId, repoId)
}