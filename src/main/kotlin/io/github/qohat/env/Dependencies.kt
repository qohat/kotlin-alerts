package io.github.qohat.env

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import io.github.qohat.repo.RepositoryRepo
import io.github.qohat.repo.UserRepo
import io.github.qohat.repo.repositoryRepo
import io.github.qohat.repo.userRepo
import kotlinx.coroutines.Dispatchers

//import io.github.qohat.repo.userPersistence

class Dependencies(val userRepo: UserRepo, val repositoryRepo: RepositoryRepo)

fun dependencies(env: Env): Resource<Dependencies> = resource {
    val hikari = hikari(env.dataSource).bind()
    val sqlDelight = sqlDelight(hikari).bind()
    val userRepo = userRepo(sqlDelight.usersQueries)
    val repositoryRepo = repositoryRepo(sqlDelight.repositoriesQueries)
    Dependencies(userRepo, repositoryRepo)
}