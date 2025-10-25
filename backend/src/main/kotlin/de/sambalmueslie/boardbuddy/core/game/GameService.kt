package de.sambalmueslie.boardbuddy.core.game

import de.sambalmueslie.boardbuddy.core.common.BaseEntityService
import de.sambalmueslie.boardbuddy.core.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.game.api.GameDescriptionValidationFailed
import de.sambalmueslie.boardbuddy.core.game.api.GameNameValidationFailed
import de.sambalmueslie.boardbuddy.core.game.db.GameData
import de.sambalmueslie.boardbuddy.core.game.db.GameRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameService(
    repository: GameRepository,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<Game, GameChangeRequest, GameData>(repository, eventService, Game::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(GameService::class.java)
    }

    override fun convert(data: GameData): Game {
        return data.convert()
    }

    override fun createData(request: GameChangeRequest): GameData {
        return GameData(0, request.name, request.description, timeProvider.currentTime())
    }

    override fun updateData(existing: GameData, request: GameChangeRequest): GameData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: GameChangeRequest) {
        if (request.name.isBlank()) throw GameNameValidationFailed(request.name)
        if (request.description.isBlank()) throw GameDescriptionValidationFailed(request.description)
    }
}