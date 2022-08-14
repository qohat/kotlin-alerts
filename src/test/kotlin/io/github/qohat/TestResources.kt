import arrow.core.*
import arrow.core.continuations.AtomicRef
import arrow.core.continuations.update
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Platform
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.bracketCase
import arrow.fx.coroutines.continuations.ResourceScope
import arrow.fx.coroutines.continuations.resource
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
class TestResource<A>(private val resource: Resource<A>) :
    ProjectListener, ResourceScope, ReadOnlyProperty<Any?, A> {

    constructor(block: suspend ResourceScope.() -> Resource<A>) : this(resource { block().bind() })

    private val value: AtomicRef<Option<A>> = AtomicRef(None)
    private val finalizers: AtomicRef<List<suspend (ExitCase) -> Unit>> = AtomicRef(emptyList())

    @Suppress("DEPRECATION")
    override suspend fun <A> Resource<A>.bind(): A =
        when (this) {
            is Resource.Dsl -> dsl.invoke(this@TestResource)
            is Resource.Allocate ->
                bracketCase(
                    {
                        val a = acquire()
                        val finalizer: suspend (ExitCase) -> Unit = { ex: ExitCase -> release(a, ex) }
                        finalizers.update { it + finalizer }
                        a
                    },
                    ::identity,
                    { a, ex ->
                        // Only if ExitCase.Failure, or ExitCase.Cancelled during acquire we cancel
                        // Otherwise we've saved the finalizer, and it will be called from somewhere else.
                        if (ex != ExitCase.Completed) {
                            val e = finalizers.get().cancelAll(ex)
                            val e2 = kotlin.runCatching { release(a, ex) }.exceptionOrNull()
                            Platform.composeErrors(e, e2)?.let { throw it }
                        }
                    }
                )

            is Resource.Bind<*, *> -> {
                val dsl: suspend ResourceScope.() -> A = {
                    val any = source.bind()
                    @Suppress("UNCHECKED_CAST") val ff = f as (Any?) -> Resource<A>
                    ff(any).bind()
                }
                dsl(this@TestResource)
            }

            is Resource.Defer -> resource().bind()
        }

    @Suppress("TooGenericExceptionThrown")
    override fun getValue(thisRef: Any?, property: KProperty<*>): A =
        value.get().getOrElse { throw RuntimeException("Attempting to access resource beforeProject $value") }

    override suspend fun beforeProject() {
        super.beforeProject()
        resource.use { println("Qohat $it") }
        value.set(Some(resource.bind()))
    }

    override suspend fun afterProject() {
        super.afterProject()
        finalizers.get().cancelAll(ExitCase.Completed)
    }
}

private suspend fun List<suspend (ExitCase) -> Unit>.cancelAll(
    exitCase: ExitCase,
    first: Throwable? = null
): Throwable? =
    fold(first) { acc, finalizer ->
        val other = kotlin.runCatching { finalizer(exitCase) }.exceptionOrNull()
        other?.let {
            acc?.apply { addSuppressed(other) } ?: other
        } ?: acc
    }