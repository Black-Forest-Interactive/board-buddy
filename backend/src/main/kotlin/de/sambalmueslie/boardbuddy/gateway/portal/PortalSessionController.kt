package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalCreateSessionRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalJoinSessionRequest
import de.sambalmueslie.boardbuddy.gateway.portal.security.PortalPlayerAuthenticationFetcher
import de.sambalmueslie.boardbuddy.gateway.portal.security.playerId
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.tags.Tag

@Secured(PortalPlayerAuthenticationFetcher.ROLE)
@Controller(value = "/api/portal/session")
@Tag(name = "Portal Session API")
class PortalSessionController(private val gateway: PortalSessionGateway) {

    @Get
    fun getSessions(authentication: Authentication) = gateway.getSessions(authentication.playerId())

    @Get("/games")
    fun getGames() = gateway.getGames()

    @Post
    fun createSession(authentication: Authentication, @Body request: PortalCreateSessionRequest) =
        gateway.createSession(authentication.playerId(), request)

    @Post("/join/{key}")
    fun joinSession(authentication: Authentication, key: String, @Body request: PortalJoinSessionRequest) =
        gateway.joinSession(authentication.playerId(), key, request)
}
