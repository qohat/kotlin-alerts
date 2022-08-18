package io.github.qohat.kafka

import io.github.nomisRev.kafka.ProducerSettings
import io.github.nomisRev.kafka.produce
import io.github.nomisRev.kafka.receiver.KafkaReceiver
import io.github.nomisRev.kafka.receiver.ReceiverRecord
import io.github.nomisRev.kafka.receiver.ReceiverSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

data class Topic(val value: String)

object Kafka {
    fun <K, V> producer(record: ProducerRecord<K, V>, settings: ProducerSettings<K, V>): Flow<RecordMetadata> =
            flow { emit(record) }
            .produce(settings)
            .flowOn(Dispatchers.IO)

    fun <K, V> consumer(settings: ReceiverSettings<K, V>, topic: Topic): Flow<ReceiverRecord<K, V>> =
        KafkaReceiver(settings)
            .receive(topic.value)
            .flowOn(Dispatchers.Unconfined)
}