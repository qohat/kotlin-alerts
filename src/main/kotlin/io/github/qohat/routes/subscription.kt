package io.github.qohat.routes

import arrow.core.continuations.either
import io.github.qohat.DomainError
import io.github.qohat.codec.Codecs
import io.github.qohat.service.DomainErrors
import io.github.qohat.service.SubscriptionService
import io.github.qohat.service.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Subscription(
    val organization: String,
    val repository: String,
    @Serializable(Codecs.LocalDateTimeSerializer::class)
    val subscribedAt: LocalDateTime
)

fun Application.subscriptionRoutes() = routing {
    route("subscription") {
        get("{userId}") {
            either {
                val id = receiveParamCatching("userId").bind()
                SubscriptionService.getBy(UserId(id))
                    .map { Subscription(it.organization, it.repository, it.subscribedAt) }
            }.respond(HttpStatusCode.OK)
        }
        put("{userId}") {
            either {
                val id = receiveParamCatching("userId").bind()
                val subscription = receiveCatching<Subscription>().bind()
                SubscriptionService.add(UserId(id), subscription)
            }.respond(HttpStatusCode.Created)
        }
        delete("{userId}") {
            either {
                val id = receiveParamCatching("userId").bind()
                SubscriptionService.delete(UserId(id))
            }.respond(HttpStatusCode.OK)
        }
    }
}