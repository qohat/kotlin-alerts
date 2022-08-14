package io.github.qohat.env

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import arrow.fx.coroutines.fromCloseable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.qohat.repo.RepoId
import io.github.qohat.repo.SlackUserId
import io.github.qohat.repo.UserId
import io.github.qohat.sqldelight.SqlDelight
import iogithubqohat.Repositories
import iogithubqohat.Subscriptions
import iogithubqohat.Users
import org.flywaydb.core.Flyway
import java.time.OffsetDateTime
import javax.sql.DataSource

fun hikari(env: Env.DataSource): Resource<HikariDataSource> =
    Resource.fromCloseable {
        HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = env.url
                username = env.username
                password = env.password
                driverClassName = env.driver
            }
        )
    }

fun sqlDelight(env: Env): Resource<SqlDelight> = resource {
    val dataSource = hikari(env.dataSource).bind()
    val driver = Resource.fromCloseable(dataSource::asJdbcDriver).bind()
    Flyway.configure().dataSource(dataSource).schemas(env.dataSource.schema).load().migrate()
    SqlDelight.Schema.create(driver)
    SqlDelight(
        driver,
        Repositories.Adapter(repoIdAdapter),
        Subscriptions.Adapter(userIdAdapter, repoIdAdapter, offsetDateTimeAdapter),
        Users.Adapter(userIdAdapter, slackUserIdAdapter)
    )
}

private val repoIdAdapter = columnAdapter(::RepoId, RepoId::id)
private val userIdAdapter = columnAdapter(::UserId, UserId::id)
private val slackUserIdAdapter = columnAdapter(::SlackUserId, SlackUserId::id)
private val offsetDateTimeAdapter = columnAdapter(OffsetDateTime::parse, OffsetDateTime::toString)

private inline fun <A : Any, B> columnAdapter(
    crossinline decode: (databaseValue: B) -> A,
    crossinline encode: (value: A) -> B
): ColumnAdapter<A, B> =
    object : ColumnAdapter<A, B> {
        override fun decode(databaseValue: B): A = decode(databaseValue)
        override fun encode(value: A): B = encode(value)
    }