package io.github.qohat.kafka

import io.github.nomisRev.kafka.produce
import io.github.qohat.env.Env
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.Serializable
import org.apache.kafka.clients.producer.ProducerRecord

@Serializable
data class SubscriptionKey(val id: String)

object Subscriptions {
    context(Kafka)
    suspend fun publish(config: Env.Kafka, event: Event) {
        val record = ProducerRecord(config.subscriptionTopic, SubscriptionKey("subscriptions"), event)
        val settings = config.producerSettings(SubscriptionKey.serializer(), Event.serializer())
        producer(record, settings).collect(::println)
    }
}
