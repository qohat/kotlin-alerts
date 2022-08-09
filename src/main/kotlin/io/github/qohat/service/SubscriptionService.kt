package io.github.qohat.service

import io.github.qohat.codec.Codecs
import io.github.qohat.repo.*
import io.github.qohat.routes.Subscription
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

object SubscriptionService {
    context(SubscriptionRepo, DomainErrors)
    suspend fun getBy(userId: UserId): List<UserSubscription> {
        TODO()
    }
    suspend fun update(userId: UserId, subscription: Subscription) {
        TODO()
    }
    suspend fun delete(userId: UserId) {
        TODO()
    }
    suspend fun save(userId: UserId, repoId: RepoId) {
        TODO()
    }

}