package io.github.qohat.env

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource

class Dependencies()

fun dependencies(env: Env): Resource<Dependencies> = TODO()/*resource {
    val hikari = hikari(env.dataSource).bind()
    val sqlDelight = sqlDelight(hikari).bind()
    val userRepo = userPersistence(sqlDelight.usersQueries)
    val articleRepo = articleRepo(sqlDelight.articlesQueries, sqlDelight.tagsQueries)
    val jwtService = jwtService(env.auth, userRepo)
    val slugGenerator = slugifyGenerator()
    val userService = userService(userRepo, jwtService)
    Dependencies(
    )
}*/