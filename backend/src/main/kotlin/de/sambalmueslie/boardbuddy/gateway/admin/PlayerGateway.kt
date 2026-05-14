package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
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
    fun create(request: PlayerChangeRequest) = service.create(request)
    fun update(id: Long, request: PlayerChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)
}