package io.github.qohat.service

import io.github.qohat.repo.RepoId
import io.github.qohat.repo.SubscriptionRepo
import io.github.qohat.repo.UserId
import io.github.qohat.repo.UserSubscription
import java.time.OffsetDateTime

context(SubscriptionRepo, DomainErrors)
object SubscriptionService {
    suspend fun getBy(userId: UserId): List<UserSubscription> = findBy(userId)
    suspend fun remove(userId: UserId, repoId: RepoId) =
        delete(userId, repoId)
    suspend fun save(userId: UserId, repoId: RepoId) =
        insert(userId, repoId, "Nothing", OffsetDateTime.now()).bind()

}