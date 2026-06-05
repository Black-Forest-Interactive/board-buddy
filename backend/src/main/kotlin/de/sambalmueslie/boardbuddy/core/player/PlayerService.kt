package de.sambalmueslie.boardbuddy.core.player

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.player.api.PlayerNameValidationFailed
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.core.player.db.PlayerData
import de.sambalmueslie.boardbuddy.core.player.db.PlayerRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class PlayerService(
    private val repository: PlayerRepository,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<Player, PlayerChangeRequest, PlayerData>(repository, eventService, Player::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(PlayerService::class.java)
        private const val AI_PLAYER_NAME = "Barbarian"
    }

    override fun convert(data: PlayerData): Player {
        return data.convert()
    }

    override fun createData(request: PlayerChangeRequest): PlayerData {
        return PlayerData(0, request.name, request.type, timeProvider.currentTime())
    }

    override fun updateData(existing: PlayerData, request: PlayerChangeRequest): PlayerData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: PlayerChangeRequest) {
        if (request.name.isBlank()) throw PlayerNameValidationFailed(request.name)
    }

    fun getAiPlayer(): Player {
        val existing = repository.findByType(PlayerType.AI).firstOrNull()
        if (existing != null) return existing.convert()

        return create(PlayerChangeRequest(PlayerType.AI, AI_PLAYER_NAME))
    }
}