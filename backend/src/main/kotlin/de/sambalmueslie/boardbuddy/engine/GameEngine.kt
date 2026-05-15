package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.model.GameEntityModel
import de.sambalmueslie.boardbuddy.engine.system.*
import de.sambalmueslie.boardbuddy.workflow.api.BattleType
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameEngine(
    private val entityModel: GameEntityModel,
    private val createUnitSystem: CreateUnitSystem,
    private val createPlayerSystem: CreatePlayerSystem,
    private val combatSystem: CombatSystem,
    private val startPlayerSystem: StartPlayerSystem,
    private val battleHandSystem: BattleHandSystem,
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
    private val governmentModel = componentModelService.get(Government::class)


    fun createUnit(unitDefinition: UnitDefinition): GameEntity {
        val entity = createUnitSystem.create(unitDefinition)
        componentModelService.persist(entity)
        return entity
    }

    fun createPlayer(nation: NationType): GameEntity {
        val entity = createPlayerSystem.create(nation)
        componentModelService.persist(entity)
        return entity
    }

    fun combat(attackingUnitId: Long, defendingUnitId: Long) {
        val attacker = entityModel.get(attackingUnitId) ?: return
        val defender = entityModel.get(defendingUnitId) ?: return
        combatSystem.combat(attacker, defender)
    }


    fun getUnit(entity: GameEntity): GameUnit {
        val damage = damageModel.get(entity)
        val health = healthModel.get(entity)
        val level = levelModel.get(entity)
        val type = typeModel.get(entity)
        val counterType = counterTypeModel.get(entity)
        return GameUnit(entity, damage, health, level, type, counterType)
    }

    fun determineStartPlayer(attacker: GameSessionPlayer, defender: GameSessionPlayer, type: BattleType, isWalled: Boolean): GameSessionPlayer {
        return startPlayerSystem.determine(attacker, defender, type, isWalled)
    }

    fun determineAttackerUnits(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>): List<GameEntity> {
        return determineBattleUnits(participant, armyCount, type, units, true)
    }

    fun determineDefenderUnits(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>): List<GameEntity> {
        return determineBattleUnits(participant, armyCount, type, units, false)
    }

    private fun determineBattleUnits(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>, isAttacker: Boolean): List<GameEntity> {
        return battleHandSystem.determine(participant, armyCount, type, units, isAttacker)
    }


}