package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.unit.UnitInstanceService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstance
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstanceChangeRequest
import de.sambalmueslie.boardbuddy.engine.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.random.Random

@Singleton
class GameEngine(
    private val unitInstanceService: UnitInstanceService,
    private val entityService: GameEntityService,
    private val storeService: ComponentStoreService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(GameEngine::class.java)
    }


    fun createUnit(player: Player, session: GameSession, unitDefinition: UnitDefinition): UnitInstance {
        // TODO calculate damage and health more smart
        val damage = Random.nextInt(unitDefinition.damagePoints.min, unitDefinition.damagePoints.max + 1)
        val health = Random.nextInt(unitDefinition.healthPoints.min, unitDefinition.healthPoints.max + 1)
        // TODO consider level
        val request = UnitInstanceChangeRequest(unitDefinition, damage, health, 1)
        return unitInstanceService.create(request)
    }


    fun createUnit(unitDefinition: UnitDefinition): GameEntity {
        val entity = entityService.createEntity()

        val damage = Random.nextInt(unitDefinition.damagePoints.min, unitDefinition.damagePoints.max + 1)
        storeService.get(Damage::class).create(entity) { Damage(damage) }

        val health = Random.nextInt(unitDefinition.healthPoints.min, unitDefinition.healthPoints.max + 1)
        storeService.get(Health::class).create(entity) { Health(health) }

        val level = 1
        storeService.get(Level::class).create(entity) { Level(level) }

        val type = unitDefinition.unitType
        storeService.get(Type::class).create(entity) { Type(type) }

        val counterType = unitDefinition.counterType
        if (counterType != null) storeService.get(CounterType::class).create(entity) { CounterType(counterType) }

        return entity
    }

}