package io.github.qohat.http

import arrow.core.Either
import io.github.qohat.DomainError
import io.github.qohat.Unexpected
import io.github.qohat.env.Env
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

data class GithubRepo(val owner: String, val name: String)

interface GithubClient {
    suspend fun exists(repo: GithubRepo): Either<DomainError, Boolean>
}


fun github(client: HttpClient, github: Env.Github) = object : GithubClient {
    override suspend fun exists(repo: GithubRepo): Either<DomainError, Boolean> =
        Either.catch {
            client.use { it.get("${github.host}/${github.repos}/${repo.owner}/${repo.name}") }
        }
        .mapLeft { t -> Unexpected("Failed calling github endpoint", t) }
        .map { response -> (response.status ==  HttpStatusCode.OK)}

}