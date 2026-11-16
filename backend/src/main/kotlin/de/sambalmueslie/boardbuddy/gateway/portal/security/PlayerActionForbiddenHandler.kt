package de.sambalmueslie.boardbuddy.gateway.portal.security

import de.sambalmueslie.boardbuddy.workflow.api.WorkflowPlayerActionForbidden
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Produces
@Singleton
@Requires(classes = [WorkflowPlayerActionForbidden::class, ExceptionHandler::class])
class PlayerActionForbiddenHandler : ExceptionHandler<WorkflowPlayerActionForbidden, HttpResponse<*>> {
    override fun handle(request: HttpRequest<*>, exception: WorkflowPlayerActionForbidden): HttpResponse<*> =
        HttpResponse.status<Any>(HttpStatus.FORBIDDEN).body(mapOf("message" to "Forbidden"))
}
