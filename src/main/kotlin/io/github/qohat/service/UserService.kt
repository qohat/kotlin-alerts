 package io.github.qohat.service

import io.github.qohat.DomainErrors
import io.github.qohat.repo.UserId
import io.github.qohat.repo.UserRepo
import io.github.qohat.routes.validate

 data class RegisterUser(val slackUserId: String, val slackChannel: String)

object UserService {
    context(UserRepo, DomainErrors)
    suspend fun register(user: RegisterUser): UserId {
        val (slackUserId, slackChannel) = user.validate().bind()
        return insert(slackUserId, slackChannel).bind()
    }
}