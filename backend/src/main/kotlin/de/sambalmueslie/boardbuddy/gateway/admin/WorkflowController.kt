package de.sambalmueslie.boardbuddy.gateway.admin

import io.micronaut.http.annotation.Controller
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/portal/workflow")
@Tag(name = "Admin Workflow API")
class WorkflowController(private val service: WorkflowGateway) {
}