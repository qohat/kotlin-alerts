package io.github.qohat

import app
import io.github.qohat.env.Dependencies
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*

/** Small DSL that exposes a setup [HttpClient] */
interface ServiceTest {
    val client: HttpClient
}

suspend fun withService(test: suspend ServiceTest.() -> Unit): Unit {
    val dep by KotestProject.dependencies
    withService(dep, test)
}

/** DSL to test MainKt server with setup [HttpClient] through [ServiceTest] */
suspend fun withService(
    dependencies: Dependencies,
    test: suspend ServiceTest.() -> Unit
): Unit =
    testApplication {
        application { app(dependencies) }
        createClient {
            expectSuccess = false
            install(ContentNegotiation) { json() }
        }
            .use { client ->
                test(
                    object : ServiceTest {
                        override val client: HttpClient = client
                    }
                )
            }
    }

// Small optimisation to avoid runBlocking from Ktor impl
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
private suspend fun testApplication(block: suspend ApplicationTestBuilder.() -> Unit) {
    val builder = ApplicationTestBuilder().apply { block() }
    val testApplication = TestApplication(builder)
    testApplication.engine.start()
    testApplication.stop()
}