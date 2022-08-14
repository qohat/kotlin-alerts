package io.github.qohat.kafka

import kotlinx.serialization.Serializable

enum class SubscriptionAction {
    SUBSCRIBED,
    UNSUBSCRIBED
}

@Serializable
sealed interface Event {
    val action: SubscriptionAction
}

class Put(val repository: String): Event {
    override val action: SubscriptionAction = SubscriptionAction.SUBSCRIBED
}

class Delete(val repository: String): Event {
    override val action: SubscriptionAction = SubscriptionAction.UNSUBSCRIBED
}

@Serializable
data class Subscribe(val repository: String): Event by Put(repository)

@Serializable
data class Unsubscribe(val repository: String): Event by Delete(repository)


