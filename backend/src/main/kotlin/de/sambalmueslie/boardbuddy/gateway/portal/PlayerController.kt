package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalPlayerChangeRequest
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller(value = "/api/portal/player")
@Tag(name = "Portal Player API")
class PlayerController(private val gateway: PlayerGateway) {

    @Get("{id}")
    fun get(id: Long) = gateway.get(id)

    @Post
    fun create(@Body request: PortalPlayerChangeRequest) = gateway.create(request)
}
