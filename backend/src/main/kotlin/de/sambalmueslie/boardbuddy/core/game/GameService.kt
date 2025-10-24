package de.sambalmueslie.boardbuddy.core.game

import de.sambalmueslie.boardbuddy.core.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.notifyCreate
import de.sambalmueslie.boardbuddy.core.event.notifyDelete
import de.sambalmueslie.boardbuddy.core.event.notifyUpdate
import de.sambalmueslie.boardbuddy.core.game.api.DescriptionValidationFailed
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.game.api.NameValidationFailed
import de.sambalmueslie.boardbuddy.core.game.db.GameData
import de.sambalmueslie.boardbuddy.core.game.db.GameRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameService(
    private val repository: GameRepository,
    private val eventService: EventService,
    private val timeProvider: TimeProvider
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameService::class.java)
    }

    private val sender = eventService.createSender(Game::class)

    fun create(request: GameChangeRequest): Game {
        validate(request)
        val data = GameData(0, request.name, request.description, timeProvider.currentTime())
        return sender.notifyCreate { repository.save(data).convert() }
    }

    fun update(id: Long, request: GameChangeRequest): Game {
        validate(request)
        val existing = repository.findByIdOrNull(id) ?: return create(request)
        existing.update(request, timeProvider.currentTime())
        return sender.notifyUpdate { repository.update(existing).convert() }
    }

    fun delete(id: Long) {
        val existing = repository.findByIdOrNull(id) ?: return
        repository.delete(existing)
        sender.notifyDelete { existing.convert() }
    }

    private fun validate(request: GameChangeRequest) {
        if (request.name.isBlank()) throw NameValidationFailed(request.name)
        if (request.description.isBlank()) throw DescriptionValidationFailed(request.description)
    }
}