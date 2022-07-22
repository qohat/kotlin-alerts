package io.github.qohat.service

import io.github.qohat.codec.Codecs
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Subscription(
    val organization: String,
    val repository: String,
    @Serializable(Codecs.LocalDateTimeSerializer::class)
    val subscribedAt: LocalDateTime
)

class UserId(val value: String)

object SubscriptionService {
    suspend fun getBy(userId: UserId): List<Subscription> =
        listOf(
            Subscription(
                organization = "47deg",
                repository = "thool",
                subscribedAt = LocalDateTime.now()
            ),
            Subscription(
                organization = "higherkindness",
                repository = "skeuomorph",
                subscribedAt = LocalDateTime.now()
            ),
            Subscription(
                organization = "47degrees",
                repository = "github4s",
                subscribedAt = LocalDateTime.now()
            )
        )
}