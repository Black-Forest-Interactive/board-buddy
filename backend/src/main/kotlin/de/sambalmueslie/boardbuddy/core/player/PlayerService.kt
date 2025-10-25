package de.sambalmueslie.boardbuddy.core.player

import de.sambalmueslie.boardbuddy.core.common.BaseEntityService
import de.sambalmueslie.boardbuddy.core.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.game.api.NameValidationFailed
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.player.db.PlayerData
import de.sambalmueslie.boardbuddy.core.player.db.PlayerRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class PlayerService(
    repository: PlayerRepository,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<Player, PlayerChangeRequest, PlayerData>(repository, eventService, Player::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(PlayerService::class.java)
    }

    override fun convert(data: PlayerData): Player {
        return data.convert()
    }

    override fun createData(request: PlayerChangeRequest): PlayerData {
        return PlayerData(0, request.name, timeProvider.currentTime())
    }

    override fun updateData(existing: PlayerData, request: PlayerChangeRequest): PlayerData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: PlayerChangeRequest) {
        if (request.name.isBlank()) throw NameValidationFailed(request.name)
    }
}