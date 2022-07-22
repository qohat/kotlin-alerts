package io.github.qohat.routes

import io.github.qohat.service.SubscriptionService
import io.github.qohat.service.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.subscriptionRoutes() = routing {
    route("subscription") {
        get("{userId}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

            call.respond(HttpStatusCode.OK, SubscriptionService.getBy(UserId(id)))
        }
    }
}