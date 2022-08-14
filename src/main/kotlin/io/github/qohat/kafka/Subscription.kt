package io.github.qohat.kafka

import io.github.qohat.env.Env
import kotlinx.serialization.builtins.serializer
import org.apache.kafka.clients.producer.ProducerRecord

object Subscriptions {
    context(Kafka)
    suspend fun publish(config: Env.Kafka, event: Event) {
        val record = ProducerRecord(config.subscriptionTopic, "subscriptions", event)
        val settings = config.producerSettings(String.serializer(), Event.serializer())
        producer(record, settings).collect(::println)
    }
}
