package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
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
}