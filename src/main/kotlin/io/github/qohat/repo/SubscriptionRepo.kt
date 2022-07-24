package io.github.qohat.repo

import java.util.*

@JvmInline
value class UserId(val id: UUID)
@JvmInline
value class SlackUserId(val id: UUID)

interface SubscriptionRepo {
}