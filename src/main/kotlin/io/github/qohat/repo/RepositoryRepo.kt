package io.github.qohat.repo

import arrow.core.Either
import io.github.qohat.UserError
import iogithubqohat.RepositoriesQueries
import java.util.UUID

@JvmInline
value class RepoId(val id: UUID)

interface RepositoryRepo {
    suspend fun insert(repository: String, owner: String): Either<UserError, RepoId>
}

fun repositoryRepo(queries: RepositoriesQueries) = object : RepositoryRepo {
    override suspend fun insert(repository: String, owner: String): Either<UserError, RepoId> {
        TODO("Not yet implemented")
    }
}