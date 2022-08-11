import io.github.qohat.env.Dependencies
import io.github.qohat.env.Env
import io.github.qohat.env.configure
import io.github.qohat.env.dependencies
import io.github.qohat.routes.subscriptionRoutes
import io.github.qohat.utils.awaitShutdown
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking(Dispatchers.Default) {
    val env = Env()
    dependencies(env).use { module ->
        embeddedServer(
            Netty,
            host = env.http.host,
            port = env.http.port
        ) { app(module) }.awaitShutdown()
    }
}

fun Application.app(module: Dependencies) {
    configure()
    with(module.userRepo) {
        with(module.repositoryRepo) {
            with(module.subscriptionRepo) {
                with(module.github) {
                    subscriptionRoutes()
                }
            }
        }
    }
}