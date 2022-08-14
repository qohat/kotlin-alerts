 package io.github.qohat.service

import io.github.qohat.DomainErrors
import io.github.qohat.repo.RepoId
import io.github.qohat.repo.RepositoryRepo
import io.github.qohat.routes.validate

 data class RegisterRepo(val owner: String, val repository: String)

object RepositoryService {
    context(RepositoryRepo, DomainErrors)
    suspend fun register(repo: RegisterRepo): RepoId {
        val (repository, owner) = repo.validate().bind()
        return insert(repository, owner).bind()
    }
}