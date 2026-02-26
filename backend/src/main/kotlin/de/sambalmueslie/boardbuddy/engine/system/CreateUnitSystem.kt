package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.model.GameEntityModel
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.random.Random

@Singleton
class CreateUnitSystem(
    private val model: GameEntityModel,
    private val componentModelService: GameComponentModelService,
) : GameSystem {
    companion object {
        private val logger = LoggerFactory.getLogger(CreateUnitSystem::class.java)
    }

    private val damageModel = componentModelService.get(Damage::class)
    private val healthModel = componentModelService.get(Health::class)
    private val levelModel = componentModelService.get(Level::class)
    private val typeModel = componentModelService.get(Type::class)
    private val counterTypeModel = componentModelService.get(CounterType::class)


    fun createUnit(unitDefinition: UnitDefinition): GameEntity {
        val entity = model.create()

        val damage = Random.nextInt(unitDefinition.damagePoints.min, unitDefinition.damagePoints.max + 1)
        damageModel.create(entity) { Damage(damage) }

        val health = Random.nextInt(unitDefinition.healthPoints.min, unitDefinition.healthPoints.max + 1)
        healthModel.create(entity) { Health(health) }

        val level = 1
        levelModel.create(entity) { Level(level) }

        val type = unitDefinition.unitType
        typeModel.create(entity) { Type(type) }

        val counterType = unitDefinition.counterType
        if (counterType != null) counterTypeModel.create(entity) { CounterType(counterType) }

        return entity
    }
}