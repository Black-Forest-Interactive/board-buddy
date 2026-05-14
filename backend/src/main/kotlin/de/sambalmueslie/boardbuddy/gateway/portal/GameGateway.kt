package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameGateway(
    private val service: GameService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: GameChangeRequest) = service.create(request)
    fun update(id: Long, request: GameChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)
}