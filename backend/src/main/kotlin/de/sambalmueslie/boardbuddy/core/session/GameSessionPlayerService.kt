package de.sambalmueslie.boardbuddy.core.session

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionData
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionPlayerRelation
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionPlayerRelationRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameSessionPlayerService(
    private val repository: GameSessionPlayerRelationRepository,
    private val playerService: PlayerService,
    eventService: EventService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameSessionPlayerService::class.java)
    }

    init {
        eventService.register(Player::class, object : EventConsumer<Player> {
            override fun created(obj: Player) {
                // intentionally left empty
            }

            override fun updated(obj: Player) {
                // intentionally left empty
            }

            override fun deleted(obj: Player) {
                repository.deleteByPlayerId(obj.id)
            }
        })
    }


    internal fun assign(gameSession: GameSessionData, player: Player) {
        val existing = repository.findByGameSessionIdAndPlayerId(gameSession.id, player.id)
        if (existing != null) return

        val relation = GameSessionPlayerRelation(gameSession.id, player.id)
        repository.save(relation)
    }

    internal fun revoke(gameSession: GameSessionData, player: Player) {
        repository.deleteByGameSessionIdAndPlayerId(gameSession.id, player.id)
    }

    internal fun getAssignedPlayers(data: GameSessionData): List<Player> {
        val relations = repository.findByGameSessionId(data.id)
        val playerIds = relations.map { it.playerId }.toSet()
        return playerService.getByIds(playerIds)
    }

    internal fun revokeAll(data: GameSessionData) {
        repository.deleteByGameSessionId(data.id)
    }
}