package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.PlayerTokenService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalPlayerChangeRequest
import jakarta.inject.Singleton

@Singleton
class PlayerGateway(
    private val service: PlayerService,
    private val tokenService: PlayerTokenService,
) {
    fun get(id: Long) = service.get(id)

    /** Creates a human player and returns it together with its one-time plain session token. */
    fun create(request: PortalPlayerChangeRequest): Pair<Player, String> {
        val player = service.create(PlayerChangeRequest(PlayerType.HUMAN, request.name))
        return player to tokenService.issue(player)
    }

    fun logout(playerId: Long) = tokenService.revoke(playerId)
}
