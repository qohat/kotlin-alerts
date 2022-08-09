package io.github.qohat

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import io.github.qohat.routes.InvalidField

sealed interface DomainError

sealed interface ValidationError : DomainError
data class EmptyUpdate(val description: String) : ValidationError
data class IncorrectInput(val errors: NonEmptyList<InvalidField>) : ValidationError {
    constructor(head: InvalidField) : this(nonEmptyListOf(head))
}

sealed interface UserError : DomainError
data class UserNotFound(val property: String) : UserError
data class SlackUserIdAlreadyExists(val id: String) : UserError

sealed interface RepositoryError: DomainError
data class RepositoryAlreadyExists(val repository: String) : RepositoryError

sealed interface SubscriptionError: DomainError
data class SubscriptionAlreadyExists(val user: String, val repo: String) : SubscriptionError
data class UserSubscriptionDoesNotExists(val user: String) : SubscriptionError

sealed interface JwtError : DomainError
data class JwtGeneration(val description: String) : JwtError
data class JwtInvalid(val description: String) : JwtError

sealed interface ArticleError: DomainError
data class CannotGenerateSlug(val description: String) : ArticleError

data class Unexpected(val description: String, val error: Throwable): UserError, RepositoryError, SubscriptionError

data class InvalidPathParam(val description: String): DomainError