package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalPlayerChangeRequest
import jakarta.inject.Singleton

@Singleton
class PlayerGateway(
    private val service: PlayerService
) {
    fun get(id: Long) = service.get(id)
    fun create(request: PortalPlayerChangeRequest) = service.create(PlayerChangeRequest(PlayerType.HUMAN, request.name))
}
