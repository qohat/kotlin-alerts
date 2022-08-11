package io.github.qohat.repo

import arrow.core.Either
import io.github.qohat.RepositoryAlreadyExists
import io.github.qohat.RepositoryError
import io.github.qohat.Unexpected
import iogithubqohat.RepositoriesQueries
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import java.util.UUID

@JvmInline
value class RepoId(val id: UUID)

interface RepositoryRepo {
    suspend fun insert(repository: String, owner: String): Either<RepositoryError, RepoId>
}

fun repositoryRepo(queries: RepositoriesQueries) = object : RepositoryRepo {
    override suspend fun insert(repository: String, owner: String): Either<RepositoryError, RepoId> =
        Either.catch {
            queries.insertAndGetId(RepoId(UUID.randomUUID()), owner, repository)
                .executeAsOne()
        }.mapLeft { error ->
            if (error is PSQLException && error.sqlState == PSQLState.UNIQUE_VIOLATION.state) {
                RepositoryAlreadyExists(repository)
            } else {
                Unexpected("Failed to persist user", error)
            }
        }
}