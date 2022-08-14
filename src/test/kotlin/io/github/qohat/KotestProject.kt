package io.github.qohat

import TestResource
import io.github.qohat.env.Env
import io.github.qohat.env.dependencies
import io.github.qohat.env.hikari
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.testcontainers.StartablePerProjectListener
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait

private class PostgreSQL : PostgreSQLContainer<PostgreSQL>("postgres:latest") {
    init { // Needed for M1
        waitingFor(Wait.forListeningPort())
    }
}

/**
 * Configuration of our Kotest Test Project.
 * It contains our Test Container configuration which is used in almost all tests.
 */
object KotestProject : AbstractProjectConfig() {
    private val postgres = StartablePerProjectListener(PostgreSQL(), "postgres")

    private val dataSource: Env.DataSource by lazy {
        Env.DataSource(
            postgres.startable.jdbcUrl,
            postgres.startable.username,
            postgres.startable.password,
            postgres.startable.driverClassName
        )
    }

    private val env: Env by lazy { Env().copy(dataSource = dataSource, ) }

    val dependencies = TestResource { dependencies(env) }
    private val hikari = TestResource { hikari(env.dataSource) }

    private val resetDatabaseListener = object : TestListener {
        override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            super.afterTest(testCase, result)
            val ds by hikari
            ds.connection.use { conn ->
                conn.prepareStatement("TRUNCATE subscriptions CASCADE").executeLargeUpdate()
            }
        }
    }

    override fun extensions(): List<Extension> =
        listOf(postgres, hikari, dependencies, resetDatabaseListener)
}