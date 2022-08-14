package io.github.qohat.http

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.fromCloseable
import io.ktor.client.HttpClient

fun httpClient() = Resource.fromCloseable {
    HttpClient()
}