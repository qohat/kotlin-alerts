package io.github.qohat.routes

import arrow.core.Either
import io.github.qohat.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable

@Serializable data class GenericErrorModel(val errors: GenericErrorModelErrors)

@Serializable data class GenericErrorModelErrors(val body: List<String>)

fun GenericErrorModel(vararg msg: String): GenericErrorModel =
    GenericErrorModel(GenericErrorModelErrors(msg.toList()))

context(PipelineContext<Unit, ApplicationCall>)

suspend inline fun <reified A : Any> Either<DomainError, A>.respond(status: HttpStatusCode): Unit =
    when (this) {
        is Either.Left -> respond(value)
        is Either.Right -> call.respond(status, value)
    }

@Suppress("ComplexMethod")
suspend fun PipelineContext<Unit, ApplicationCall>.respond(error: DomainError): Unit =
    when (error) {
        PasswordNotMatched -> call.respond(HttpStatusCode.Unauthorized)
        is IncorrectInput ->
            unprocessable(
                error.errors.joinToString { field -> "${field.field}: ${field.errors.joinToString()}" }
            )
        is Unexpected ->
            internal(
                """
        Unexpected failure occurred:
          - description: ${error.description}
          - cause: ${error.error}
        """.trimIndent()
            )
        is EmptyUpdate -> unprocessable(error.description)
        is EmailAlreadyExists -> unprocessable("${error.email} is already registered")
        is JwtGeneration -> unprocessable(error.description)
        is UserNotFound -> unprocessable("User with ${error.property} not found")
        is UsernameAlreadyExists -> unprocessable("Username ${error.username} already exists")
        is JwtInvalid -> unprocessable(error.description)
        is CannotGenerateSlug -> unprocessable(error.description)
        else -> unprocessable("Can not process this.")
    }

private suspend inline fun PipelineContext<Unit, ApplicationCall>.unprocessable(
    error: String
): Unit = call.respond(HttpStatusCode.UnprocessableEntity, GenericErrorModel(error))

private suspend inline fun PipelineContext<Unit, ApplicationCall>.internal(error: String): Unit =
    call.respond(HttpStatusCode.InternalServerError, GenericErrorModel(error))

suspend inline fun <reified A : Any> PipelineContext<
        Unit, ApplicationCall>.receiveCatching(): Either<DomainError, A> =
    Either.catch { call.receive<A>() }.mapLeft { e ->
        Unexpected(e.message ?: "Received malformed JSON for ${A::class.simpleName}", e)
    }

fun PipelineContext<Unit, ApplicationCall>.receiveParamCatching(param: String): Either<DomainError, String> =
        Either.fromNullable(call.parameters[param])
        .mapLeft { InvalidPathParam( "Received malformed or invalid param: $param") }