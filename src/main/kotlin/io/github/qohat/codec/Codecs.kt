package io.github.qohat.codec

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

class Codecs {
    object LocalDateTimeSerializer: KSerializer<LocalDateTime> {

        override fun deserialize(decoder: Decoder): LocalDateTime =
            LocalDateTime.parse(decoder.decodeString())

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: LocalDateTime) =
            encoder.encodeString(value.toString())
    }
}