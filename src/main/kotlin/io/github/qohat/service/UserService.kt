 package io.github.qohat.service

import arrow.core.Either
import arrow.core.continuations.EffectScope
import arrow.core.continuations.either
import io.github.qohat.DomainError
import io.github.qohat.repo.SlackUserId
import io.github.qohat.repo.UserId
import io.github.qohat.repo.UserRepo
import io.github.qohat.routes.validate
import java.util.UUID

 data class RegisterUser(val slackUserId: String, val slackChannel: String)

/*interface UserService {
    suspend fun register(user: RegisterUser): Either<DomainError, UserId>
}


fun userService(userRepo: UserRepo) = object : UserService {
    override suspend fun register(user: RegisterUser): Either<DomainError, UserId> =
        either {
            val (username, email, password) = user.validate().bind()
            userRepo.insert(username, email, password).bind()
        }
}*/

typealias DomainErrors = EffectScope<DomainError>

object UserService {
    context(UserRepo, DomainErrors)
    suspend fun register(user: RegisterUser): UserId {
        val (slackUserId, slackChannel) = user.validate().bind()
        return insert(slackUserId, slackChannel).bind()
    }
}