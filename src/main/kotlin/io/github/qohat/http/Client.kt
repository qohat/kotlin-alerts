package io.github.qohat.http

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.fromCloseable
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache

fun httpClient() = Resource.fromCloseable {
    HttpClient()
}