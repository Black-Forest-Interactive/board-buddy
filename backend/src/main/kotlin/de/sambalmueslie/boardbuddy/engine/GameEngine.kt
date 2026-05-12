package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.model.GameEntityModel
import de.sambalmueslie.boardbuddy.engine.system.CombatSystem
import de.sambalmueslie.boardbuddy.engine.system.CreateUnitSystem
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameEngine(
    private val entityModel: GameEntityModel,
    private val createUnitSystem: CreateUnitSystem,
    private val combatSystem: CombatSystem,
    private val componentModelService: GameComponentModelService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(GameEngine::class.java)
    }

    private val damageModel = componentModelService.get(Damage::class)
    private val healthModel = componentModelService.get(Health::class)
    private val levelModel = componentModelService.get(Level::class)
    private val typeModel = componentModelService.get(Type::class)
    private val counterTypeModel = componentModelService.get(CounterType::class)


    fun createUnit(unitDefinition: UnitDefinition): GameEntity {
        val entity = createUnitSystem.createUnit(unitDefinition)
        componentModelService.persist(entity)
        return entity
    }

    fun combat(attackerId: Long, defenderId: Long) {
        val attacker = entityModel.get(attackerId) ?: return
        val defender = entityModel.get(defenderId) ?: return
        combatSystem.combat(attacker, defender)
    }

    fun getInfo(entity: GameEntity): GameEntityInfo {
        return componentModelService.getInfo(entity)
    }

    fun getUnit(entity: GameEntity): GameUnit {
        val damage = damageModel.get(entity)
        val health = healthModel.get(entity)
        val level = levelModel.get(entity)
        val type = typeModel.get(entity)
        val counterType = counterTypeModel.get(entity)
        return GameUnit(entity, damage, health, level, type, counterType)
    }
}