package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalCreateSessionRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalJoinSessionRequest
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller(value = "/api/portal/session")
@Tag(name = "Portal Session API")
class PortalSessionController(private val gateway: PortalSessionGateway) {

    @Get
    fun getSessions(@CookieValue("player-id") playerId: Long) = gateway.getSessions(playerId)

    @Get("/games")
    fun getGames() = gateway.getGames()

    @Post
    fun createSession(@CookieValue("player-id") playerId: Long, @Body request: PortalCreateSessionRequest) =
        gateway.createSession(playerId, request)

    @Post("/join/{key}")
    fun joinSession(@CookieValue("player-id") playerId: Long, key: String, @Body request: PortalJoinSessionRequest) =
        gateway.joinSession(playerId, key, request)
}
