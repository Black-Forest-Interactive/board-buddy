package de.sambalmueslie.boardbuddy.core.session

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionData
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionPlayerRelation
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionPlayerRelationRepository
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
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


    internal fun assign(gameSession: GameSessionData, player: Player, entity: GameEntity) {
        val existing = repository.findByGameSessionIdAndPlayerId(gameSession.id, player.id)
        if (existing != null) return

        val relation = GameSessionPlayerRelation(gameSession.id, player.id, entity)
        repository.save(relation)
    }

    internal fun revoke(gameSession: GameSessionData, player: Player) {
        repository.deleteByGameSessionIdAndPlayerId(gameSession.id, player.id)
    }

    internal fun getAssignedPlayers(data: GameSessionData): List<GameSessionPlayer> {
        val relations = repository.findByGameSessionId(data.id)
        val playerIds = relations.map { it.playerId }.toSet()
        val players = playerService.getByIds(playerIds).associateBy { it.id }
        return relations.mapNotNull {
            val p = players[it.playerId] ?: return@mapNotNull null
            GameSessionPlayer(p, it.entityId)
        }
    }

    internal fun getAllEntityIds(data: GameSessionData): List<GameEntity> {
        return repository.findByGameSessionId(data.id).map { it.entityId }
    }

    internal fun revokeAll(data: GameSessionData) {
        repository.deleteByGameSessionId(data.id)
    }

    internal fun getSessionIdsByPlayer(playerId: Long): List<Long> {
        return repository.findByPlayerId(playerId).map { it.gameSessionId }
    }
}