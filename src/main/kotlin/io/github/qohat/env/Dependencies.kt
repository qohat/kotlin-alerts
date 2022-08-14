package io.github.qohat.env

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import io.github.qohat.http.Github
import io.github.qohat.http.github
import io.github.qohat.http.httpClient
import io.github.qohat.repo.RepositoryRepo
import io.github.qohat.repo.SubscriptionRepo
import io.github.qohat.repo.UserRepo
import io.github.qohat.repo.repositoryRepo
import io.github.qohat.repo.subscriptionRepo
import io.github.qohat.repo.userRepo

class Dependencies(val userRepo: UserRepo, val repositoryRepo: RepositoryRepo, val subscriptionRepo: SubscriptionRepo, val github: Github)
fun dependencies(env: Env): Resource<Dependencies> = resource {
    val sqlDelight = sqlDelight(env).bind()
    val userRepo = userRepo(sqlDelight.usersQueries)
    val repositoryRepo = repositoryRepo(sqlDelight.repositoriesQueries)
    val subscriptionRepo = subscriptionRepo(sqlDelight.subscriptionsQueries)
    val httpClient = httpClient().bind()
    val github = github(httpClient, env.github)
    Dependencies(userRepo, repositoryRepo, subscriptionRepo, github)
}