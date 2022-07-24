package io.github.qohat.repo

import arrow.core.Either
import io.github.qohat.UserError
import java.util.UUID

@JvmInline
value class RepoId(val id: UUID)

interface RepositoryRepo {
    suspend fun insert(repository: String, owner: String): Either<UserError, RepoId>
}