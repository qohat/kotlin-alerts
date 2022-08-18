package io.github.qohat.kafka

import io.github.nomisRev.kafka.Admin
import io.github.nomisRev.kafka.AdminSettings
import io.github.nomisRev.kafka.commitBatchWithin
import io.github.nomisRev.kafka.createTopic
import io.github.nomisRev.kafka.receiver.KafkaReceiver
import io.github.qohat.KotestProject
import io.github.qohat.http.GithubRepo
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import org.apache.kafka.clients.admin.NewTopic

class SubscriptionSpec: StringSpec({
    val kafka by KotestProject.kafka
    val settings by lazy { kafka.receiverSettings(SubscriptionKey.serializer(), Subscribe.serializer()) }

    val repo = "arrow-kt"

    "Can publish a subscription" {
        with(Kafka) {
            Subscriptions.publish(kafka, Subscribe(repo))
            consumer(settings, Topic(kafka.subscriptionTopic))
            .take(1)
            .onEach { record ->
                record.value().action shouldBe SubscriptionAction.SUBSCRIBED
                record.value().repository shouldBe repo
                record.offset.acknowledge()
            }.collect()
        }
    }

    "Can publish when unsubscribe" {
        with(Kafka) {
            Subscriptions.publish(kafka, Unsubscribe(repo))
            consumer(settings, Topic(kafka.subscriptionTopic))
                .take(1)
                .onEach { record ->
                    record.value().action shouldBe SubscriptionAction.UNSUBSCRIBED
                    record.value().repository shouldBe repo
                    record.offset.acknowledge()
                }.collect()
        }
    }
})