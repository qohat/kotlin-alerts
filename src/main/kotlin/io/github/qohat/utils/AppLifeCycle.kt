package io.github.qohat.utils

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.addShutdownHook
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job

context(scope@CoroutineScope)
        suspend fun ApplicationEngine.awaitShutdown(
    prewait: Duration = 30.seconds,
    grace: Duration = 1.seconds,
    timeout: Duration = 5.seconds,
): Unit {
    addShutdownHook {
        // We use a CountDownLatch to back-pressure JVM exit
        val countDownLatch = CountDownLatch(1)
        suspend {
            if (!environment.developmentMode) {
                environment.log.info(
                    "prewait delay of ${prewait.inWholeMilliseconds}ms, turn it off using io.ktor.development=true"
                )
                // Safe since we're on KtorShutdownHook Thread. Avoids additional shifting
                Thread.sleep(prewait.inWholeMilliseconds)
            }
            environment.log.info("Shutting down HTTP server...")
            stop(grace.inWholeMilliseconds, timeout.inWholeMilliseconds)
            environment.log.info("HTTP server shutdown!")
            this@scope.coroutineContext.job.join()
            countDownLatch.countDown()
        }.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext, Result<Unit>::getOrThrow))
        countDownLatch.await(
            prewait.inWholeMilliseconds + grace.inWholeMilliseconds + (2 * timeout.inWholeMilliseconds),
            TimeUnit.MILLISECONDS
        )
    }
    start(wait = true)
}