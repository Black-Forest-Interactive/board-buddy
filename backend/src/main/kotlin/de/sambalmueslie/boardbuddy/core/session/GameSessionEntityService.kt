package de.sambalmueslie.boardbuddy.core.session

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionData
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionEntityRelation
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionEntityRelationRepository
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameSessionEntityService(
    private val repository: GameSessionEntityRelationRepository,
    eventService: EventService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameSessionEntityService::class.java)
    }


    init {
        eventService.register(GameEntity::class, object : EventConsumer<GameEntity> {
            override fun created(obj: GameEntity) {
                // intentionally left empty
            }

            override fun updated(obj: GameEntity) {
                // intentionally left empty
            }

            override fun deleted(obj: GameEntity) {
                repository.deleteByEntityId(obj)
            }
        })
    }

    internal fun assign(gameSession: GameSessionData, player: Player, entity: GameEntity) {
        val existing = repository.findByGameSessionIdAndPlayerIdAndEntityId(gameSession.id, player.id, entity)
        if (existing != null) return

        val relation = GameSessionEntityRelation(gameSession.id, player.id, entity)
        repository.save(relation)
    }

    internal fun revoke(gameSession: GameSessionData, player: Player, entity: GameEntity) {
        repository.deleteByGameSessionIdAndPlayerIdAndEntityId(gameSession.id, player.id, entity)
    }

    internal fun get(data: GameSessionData, player: Player): List<GameEntity> {
        val relations = repository.findByGameSessionIdAndPlayerId(data.id, player.id)
        return relations.map { it.entityId }
    }

    internal fun revokeAll(data: GameSessionData) {
        repository.deleteByGameSessionId(data.id)
    }

}