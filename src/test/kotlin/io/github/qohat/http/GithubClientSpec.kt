package io.github.qohat.http

import io.github.qohat.RepositoryDoesNotExists
import io.github.qohat.env.Env
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

class GithubClientSpec: StringSpec({
    val github = Env.Github()
    "Should return true for valid repo" {
        val client = HttpClient(CIO)
        val response = github(client, github).exists(GithubRepo("qohat", "kotlin-streaming"))
        response shouldBeRight true
    }

    "Should be left for an invalid repo" {
        val client = HttpClient(CIO)
        val repo = GithubRepo("qohat", "kotlin-streaming-fake")
        val response = github(client, github).exists(repo)
        response shouldBeLeft RepositoryDoesNotExists(repo.owner, repo.name)
    }
})