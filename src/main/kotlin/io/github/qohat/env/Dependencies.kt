package io.github.qohat.env

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import io.github.qohat.repo.UserRepo
import io.github.qohat.repo.gituserPersistence

class Dependencies(val userRepo: UserRepo)

fun dependencies(env: Env): Resource<Dependencies> = resource {
    val hikari = hikari(env.dataSource).bind()
    val sqlDelight = sqlDelight(hikari).bind()
    val userRepo = userPersistence(sqlDelight.usersQueries)
    Dependencies(userRepo)
}