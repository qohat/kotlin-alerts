package io.github.qohat.env

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import arrow.fx.coroutines.fromCloseable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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

fun sqlDelight(dataSource: DataSource): Resource<SqlDelight> = resource {
    val driver = Resource.fromCloseable(dataSource::asJdbcDriver).bind()
}