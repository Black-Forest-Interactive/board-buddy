package de.sambalmueslie.boardbuddy.common

import de.sambalmueslie.boardbuddy.workflow.api.WorkflowInvalidId
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowPlayerActionForbidden
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

/** Domain errors: the message only contains ids and (shortened) names, so it is safe to return. */
@Produces
@Singleton
class EntityExceptionHandler : ExceptionHandler<EntityException, HttpResponse<*>> {
    override fun handle(request: HttpRequest<*>, exception: EntityException): HttpResponse<*> {
        val status = when (exception) {
            is WorkflowPlayerActionForbidden -> HttpStatus.FORBIDDEN
            is WorkflowInvalidId -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }
        val message = if (status == HttpStatus.FORBIDDEN) "Forbidden" else exception.message ?: status.reason
        return HttpResponse.status<Any>(status).body(mapOf("message" to message))
    }
}

/**
 * Everything without a more specific handler. Micronaut would otherwise return the exception message,
 * which can contain SQL or internal details. The details only go to the log.
 */
@Produces
@Singleton
class UnexpectedExceptionHandler : ExceptionHandler<RuntimeException, HttpResponse<*>> {
    companion object {
        private val logger = LoggerFactory.getLogger(UnexpectedExceptionHandler::class.java)
    }

    override fun handle(request: HttpRequest<*>, exception: RuntimeException): HttpResponse<*> {
        logger.error("Unexpected error on {} {}", request.method, request.path, exception)
        return HttpResponse.serverError<Any>().body(mapOf("message" to "Internal Server Error"))
    }
}
