package io.github.qohat.kafka

import io.github.nomisRev.kafka.Admin
import io.github.nomisRev.kafka.AdminSettings
import io.github.nomisRev.kafka.commitBatchWithin
import io.github.nomisRev.kafka.createTopic
import io.github.nomisRev.kafka.receiver.KafkaReceiver
import io.github.qohat.KotestProject
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

    "Can publish a subscription" {
        with(Kafka) {
            Subscriptions.publish(kafka, Subscribe("arrow-kt"))
            KafkaReceiver(settings)
                .receive(kafka.subscriptionTopic)
                .take(1)
                .onEach { record ->
                    record.value().action shouldBe SubscriptionAction.SUBSCRIBED
                    record.value().repository shouldBe "arrow-kt"
                    record.offset.acknowledge()
                }.collect()
        }
    }
})