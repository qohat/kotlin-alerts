package io.github.qohat.service

import arrow.core.Either
import arrow.core.continuations.either
import io.github.qohat.DomainError
import io.github.qohat.repo.UserId
import io.github.qohat.repo.UserRepo
import io.github.qohat.routes.validate


data class RegisterUser(val username: String, val email: String, val password: String)

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

object UserService {
    context(UserRepo)
    suspend fun register(user: RegisterUser): Either<DomainError, UserId> =
        either {
            val (username, email, password) = user.validate().bind()
            insert(username, email, password).bind()
        }
}