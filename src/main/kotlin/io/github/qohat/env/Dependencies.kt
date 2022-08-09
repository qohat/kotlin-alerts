package io.github.qohat.env

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import io.github.qohat.http.Github
import io.github.qohat.http.github
import io.github.qohat.repo.*
import io.ktor.client.*
class Dependencies(val userRepo: UserRepo, val repositoryRepo: RepositoryRepo, val subscriptionRepo: SubscriptionRepo, val githubClient: Github)
fun dependencies(env: Env): Resource<Dependencies> = resource {
    val hikari = hikari(env.dataSource).bind()
    val sqlDelight = sqlDelight(hikari).bind()
    val userRepo = userRepo(sqlDelight.usersQueries)
    val repositoryRepo = repositoryRepo(sqlDelight.repositoriesQueries)
    val subscriptionRepo = subscriptionRepo(sqlDelight.subscriptionsQueries)
    val httpClient = HttpClient()
    val githubClient = github(httpClient, env.github)
    Dependencies(userRepo, repositoryRepo, subscriptionRepo, githubClient)
}