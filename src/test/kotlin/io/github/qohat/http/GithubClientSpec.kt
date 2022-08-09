package io.github.qohat.http

import io.github.qohat.env.Env
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.*
import io.ktor.client.engine.cio.*

class GithubClientSpec: FunSpec() {
    init {
        val github = Env.Github()
        test("Should return true for valid repo") {
            val client = HttpClient(CIO)
            val response = github(client, github).exists(GithubRepo("qohat", "kotlin-streaming"))
            response shouldBeRight true
        }

        test("Should return false for invalid repo") {
            val client = HttpClient(CIO)
            val response = github(client, github).exists(GithubRepo("qohat", "kotlin-streaming-fake"))
            response shouldBeRight false
        }
    }
}