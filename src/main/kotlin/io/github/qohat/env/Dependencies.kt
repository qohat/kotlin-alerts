package io.github.qohat.env

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import arrow.fx.coroutines.fromCloseable
import io.github.qohat.http.Github
import io.github.qohat.http.github
import io.github.qohat.repo.RepositoryRepo
import io.github.qohat.repo.SubscriptionRepo
import io.github.qohat.repo.UserRepo
import io.github.qohat.repo.repositoryRepo
import io.github.qohat.repo.subscriptionRepo
import io.github.qohat.repo.userRepo
import io.ktor.client.HttpClient

class Dependencies(val userRepo: UserRepo, val repositoryRepo: RepositoryRepo, val subscriptionRepo: SubscriptionRepo, val github: Github)
fun dependencies(env: Env): Resource<Dependencies> = resource {
    val hikari = hikari(env.dataSource).bind()
    val sqlDelight = sqlDelight(hikari).bind()
    val userRepo = userRepo(sqlDelight.usersQueries)
    val repositoryRepo = repositoryRepo(sqlDelight.repositoriesQueries)
    val subscriptionRepo = subscriptionRepo(sqlDelight.subscriptionsQueries)
    val httpClient = Resource.fromCloseable { HttpClient() }.bind()
    val github = github(httpClient, env.github)
    Dependencies(userRepo, repositoryRepo, subscriptionRepo, github)
}