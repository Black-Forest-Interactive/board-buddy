package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalPlayerChangeRequest
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class PlayerGateway(
    private val service: PlayerService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PlayerGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: PortalPlayerChangeRequest) = service.create(PlayerChangeRequest(PlayerType.HUMAN, request.name))
    fun update(id: Long, request: PortalPlayerChangeRequest) = service.update(id, PlayerChangeRequest(PlayerType.HUMAN, request.name))
    fun delete(id: Long) = service.delete(id)
}