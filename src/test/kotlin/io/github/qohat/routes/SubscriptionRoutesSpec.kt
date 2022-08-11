package io.github.qohat.routes

import io.github.qohat.KotestProject
import io.github.qohat.repo.UserId
import io.github.qohat.repo.UserSubscription
import io.github.qohat.withService
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.OffsetDateTime
import java.util.UUID

class SubscriptionRoutesSpec: StringSpec({
    val dependencies by KotestProject.dependencies
    val userRepo by lazy { dependencies.userRepo }
    val repositoryRepo by lazy { dependencies.repositoryRepo }
    val subscriptionRepo by lazy { dependencies.subscriptionRepo }
    val github by lazy { dependencies.github }

    "Can get the subscriptions by user" {
        val userId = userRepo.insert("slackId", "slackChanel").shouldBeRight()
        val repoId = repositoryRepo.insert("kotlin-streaming", "qohat").shouldBeRight()
        withService(dependencies) {
            subscriptionRepo.insert(userId, repoId, "Nothing", OffsetDateTime.now())
            val response = client.get("/subscription/${userId.id}")
            response.status shouldBe HttpStatusCode.OK
            val subscriptions = response.body<List<UserSubscription>>()
            subscriptions.isNotEmpty() shouldBe true
        }
    }

    "Can put subscriptions by user" {
        val userId = UserId(UUID.randomUUID())
        withService(dependencies) {
            val response = client.put("/subscription/${userId.id}") {
                contentType(ContentType.Application.Json)
                setBody(
                    SubscriptionWrapper(
                        Subscription(repository = "kotlin-streaming", organization = "qohat")
                    )
                )
            }
            response.status shouldBe HttpStatusCode.Created
        }
    }
})