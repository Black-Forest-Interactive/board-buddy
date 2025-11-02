package de.sambalmueslie.boardbuddy.core.session

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionData
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionUnitRelation
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionUnitRelationRepository
import de.sambalmueslie.boardbuddy.core.unit.UnitInstanceService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstance
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameSessionUnitService(
    private val repository: GameSessionUnitRelationRepository,
    private val unitService: UnitInstanceService,
    eventService: EventService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameSessionUnitService::class.java)
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

        eventService.register(UnitInstance::class, object : EventConsumer<UnitInstance> {
            override fun created(obj: UnitInstance) {
                // intentionally left empty
            }

            override fun updated(obj: UnitInstance) {
                // intentionally left empty
            }

            override fun deleted(obj: UnitInstance) {
                repository.deleteByUnitInstanceId(obj.id)
            }
        })
    }

    fun assign(gameSession: GameSessionData, player: Player, instance: UnitInstance) {
        val existing = repository.findByGameSessionIdAndPlayerIdAndUnitInstanceId(gameSession.id, player.id, instance.id)
        if (existing != null) return

        val relation = GameSessionUnitRelation(gameSession.id, player.id, instance.id)
        repository.save(relation)
    }

    internal fun revoke(gameSession: GameSessionData, player: Player, instance: UnitInstance) {
        repository.deleteByGameSessionIdAndPlayerIdAndUnitInstanceId(gameSession.id, player.id, instance.id)
    }

    internal fun getAssignedUnits(data: GameSessionData, player: Player): List<UnitInstance> {
        val relations = repository.findByGameSessionIdAndPlayerId(data.id, player.id)
        val unitIds = relations.map { it.unitInstanceId }.toSet()
        return unitService.getByIds(unitIds)
    }

    internal fun revokeAll(data: GameSessionData) {
        repository.deleteByGameSessionId(data.id)
    }

}