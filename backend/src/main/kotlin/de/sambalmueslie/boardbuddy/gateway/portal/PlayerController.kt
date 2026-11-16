package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalPlayerChangeRequest
import de.sambalmueslie.boardbuddy.gateway.portal.security.PortalPlayerAuthenticationFetcher
import de.sambalmueslie.boardbuddy.gateway.portal.security.PortalPlayerCookie
import de.sambalmueslie.boardbuddy.gateway.portal.security.playerId
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/portal/player")
@Tag(name = "Portal Player API")
class PlayerController(
    private val gateway: PlayerGateway,
    private val cookie: PortalPlayerCookie,
) {

    @Secured(PortalPlayerAuthenticationFetcher.ROLE)
    @Get("me")
    fun me(authentication: Authentication): Player? = gateway.get(authentication.playerId())

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post
    fun create(@Body request: PortalPlayerChangeRequest): HttpResponse<Player> {
        val (player, token) = gateway.create(request)
        return HttpResponse.ok(player).cookie(cookie.create(token))
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("logout")
    fun logout(authentication: Authentication?): HttpResponse<Unit> {
        authentication?.let { gateway.logout(it.playerId()) }
        return HttpResponse.noContent<Unit>().cookie(cookie.clear())
    }
}
