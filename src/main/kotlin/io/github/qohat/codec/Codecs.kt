package io.github.qohat.codec

import arrow.core.Either
import arrow.core.computations.ResultEffect.bind
import io.github.qohat.repo.UserId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.UUID

class Codecs {
    object OffsetDateTimeSerializer: KSerializer<OffsetDateTime> {

        override fun deserialize(decoder: Decoder): OffsetDateTime =
            OffsetDateTime.parse(decoder.decodeString())

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: OffsetDateTime) =
            encoder.encodeString(value.toString())
    }

    object UserIdSerializer: KSerializer<UserId> {

        override fun deserialize(decoder: Decoder): UserId =
            Either.catch {
                decoder.decodeString()
                .replace("UserId(id=", "")
                .replace(")", "")
            }
            .map { UUID.fromString(it) }
            .map { UserId(it) }
            .bind()

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UserId", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: UserId) =
            encoder.encodeString(value.toString())
    }
}