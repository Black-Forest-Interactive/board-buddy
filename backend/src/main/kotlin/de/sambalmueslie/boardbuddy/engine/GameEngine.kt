package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.db.GameComponentStorage
import de.sambalmueslie.boardbuddy.engine.db.GameEntityStorage
import de.sambalmueslie.boardbuddy.engine.system.CombatSystem
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.random.Random

@Singleton
class GameEngine(
    private val unitInstanceService: UnitInstanceService,
    private val entityStorage: GameEntityStorage,
    private val componentStorage: GameComponentStorage,
    private val combatSystem: CombatSystem
) {

    companion object {
        private val logger = LoggerFactory.getLogger(GameEngine::class.java)
    }

    @Deprecated("use create unit instead")
    fun createUnit(player: Player, session: GameSession, unitDefinition: UnitDefinition): UnitInstance {
        // TODO calculate damage and health more smart
        val damage = Random.nextInt(unitDefinition.damagePoints.min, unitDefinition.damagePoints.max + 1)
        val health = Random.nextInt(unitDefinition.healthPoints.min, unitDefinition.healthPoints.max + 1)
        // TODO consider level
        val request = UnitInstanceChangeRequest(unitDefinition, damage, health, 1)
        return unitInstanceService.create(request)
    }


    fun createUnit(unitDefinition: UnitDefinition): GameEntity {
        val entity = entityStorage.create()

        val damage = Random.nextInt(unitDefinition.damagePoints.min, unitDefinition.damagePoints.max + 1)
        componentStorage.create(entity, Damage::class) { Damage(damage) }

        val health = Random.nextInt(unitDefinition.healthPoints.min, unitDefinition.healthPoints.max + 1)
        componentStorage.create(entity, Health::class) { Health(damage) }

        val level = 1
        componentStorage.create(entity, Level::class) { Level(level) }

        val type = unitDefinition.unitType
        componentStorage.create(entity, Type::class) { Type(type) }

        val counterType = unitDefinition.counterType
        if (counterType != null) componentStorage.create(entity, CounterType::class) { CounterType(counterType) }

        return entity
    }

    fun combat(attackerId: Long, defenderId: Long) {
        val attacker = entityStorage.get(attackerId) ?: return
        val defender = entityStorage.get(defenderId) ?: return
        combatSystem.combat(attacker, defender)
    }

}