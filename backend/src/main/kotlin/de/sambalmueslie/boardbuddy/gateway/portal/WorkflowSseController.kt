package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.gateway.portal.security.PortalPlayerAuthenticationFetcher
import de.sambalmueslie.boardbuddy.gateway.portal.security.playerId
import de.sambalmueslie.boardbuddy.workflow.sse.SessionEvent
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.sse.Event
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Flux

@Secured(PortalPlayerAuthenticationFetcher.ROLE)
@Controller("/api/portal/workflow")
@Tag(name = "Portal Workflow API")
class WorkflowSseController(private val gateway: WorkflowGateway) {

    @Get("{id}/events", produces = [MediaType.TEXT_EVENT_STREAM])
    fun events(authentication: Authentication, id: String): Flux<Event<SessionEvent>> {
        return gateway.subscribeEvents(id, authentication.playerId()).map { Event.of(it) }
    }
}
