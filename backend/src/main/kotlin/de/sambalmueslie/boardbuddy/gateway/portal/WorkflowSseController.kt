package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.workflow.sse.SessionEvent
import de.sambalmueslie.boardbuddy.workflow.sse.SessionEventService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.sse.Event
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Flux

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/portal/workflow")
@Tag(name = "Portal Workflow API")
class WorkflowSseController(private val eventService: SessionEventService) {

    @Get("{id}/events", produces = [MediaType.TEXT_EVENT_STREAM])
    fun events(id: String): Flux<Event<SessionEvent>> {
        return eventService.subscribe(id).map { Event.of(it) }
    }
}
