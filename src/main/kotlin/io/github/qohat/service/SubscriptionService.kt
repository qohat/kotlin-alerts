package io.github.qohat.service

import io.github.qohat.codec.Codecs
import io.github.qohat.routes.Subscription
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@JvmInline
value class UserId(val value: String)
data class UserSubscription(
    val organization: String,
    val repository: String,
    @Serializable(Codecs.LocalDateTimeSerializer::class)
    val subscribedAt: LocalDateTime,
    val userId: UserId
)

object SubscriptionService {

    val subscriptions = listOf(
        UserSubscription(
            organization = "47deg",
            repository = "thool",
            subscribedAt = LocalDateTime.now(),
            userId = UserId(value = "u1")
        ),
        UserSubscription(
            organization = "higherkindness",
            repository = "skeuomorph",
            subscribedAt = LocalDateTime.now(),
            userId = UserId(value = "u1")
        ),
        UserSubscription(
            organization = "47degrees",
            repository = "github4s",
            subscribedAt = LocalDateTime.now(),
            UserId(value = "u2")
        )
    )
    suspend fun getBy(userId: UserId): List<UserSubscription> =
        subscriptions.filter { it.userId == userId }
    suspend fun update(userId: UserId, subscription: Subscription) =
        subscriptions.filterNot {it.userId == userId}
            .plus(
                UserSubscription(
                    organization = subscription.organization,
                    repository = subscription.repository,
                    subscribedAt = LocalDateTime.now(),
                    userId = userId
                )
            )
    suspend fun delete(userId: UserId) =
        subscriptions.filterNot {it.userId == userId}

    suspend fun save(userId: UserId) =
        subscriptions.filterNot {it.userId == userId}

}