package io.github.qohat.env

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import io.github.qohat.repo.UserRepo
import io.github.qohat.repo.userPersistence

class Dependencies(val userRepo: String)

fun dependencies(env: Env): Resource<Dependencies> = resource {
    val hikari = hikari(env.dataSource).bind()
    val sqlDelight = sqlDelight(hikari).bind()
    Dependencies("userRepo")
}