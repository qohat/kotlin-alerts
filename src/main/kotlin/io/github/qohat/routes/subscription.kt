package io.github.qohat.routes

import arrow.core.continuations.either
import io.github.qohat.codec.Codecs
import io.github.qohat.repo.RepositoryRepo
import io.github.qohat.repo.UserId
import io.github.qohat.repo.UserRepo
import io.github.qohat.service.*
import io.ktor.http.*
import io.ktor.server.application.*
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

@Serializable
data class NewSubscription(
    val slackUserId: String,
    val slackChannel: String,
    val owner: String,
    val repository: String
)

context(UserRepo, RepositoryRepo)
fun Application.subscriptionRoutes() = routing {
    route("subscription") {
        get("{userId}") {
            either {
                val id = receiveParamCatching("userId").bind()
                //SubscriptionService.getBy(UserId(id))
            }.respond(HttpStatusCode.OK)
        }
        put("{userId}") {
            either {
                val id = receiveParamCatching("userId").bind()
                val subscription = receiveCatching<Subscription>().bind()
                //SubscriptionService.update(UserId(id), subscription)
            }.respond(HttpStatusCode.Created)
        }
        delete("{userId}") {
            either {
                val id = receiveParamCatching("userId").bind()
                //SubscriptionService.delete(UserId(id))
            }.respond(HttpStatusCode.OK)
        }
        post("/slack/command") {
            either {
                val (slackUserId, slackChannel, owner, repository) = receiveCatching<NewSubscription>().bind()
                val userId = UserService.register(RegisterUser(slackUserId, slackChannel))
                val repoId = RepositoryService.register(RegisterRepo(repository, owner))
                val subscription = SubscriptionService.save(userId, repoId)
            }.respond(HttpStatusCode.OK)
        }
    }
}