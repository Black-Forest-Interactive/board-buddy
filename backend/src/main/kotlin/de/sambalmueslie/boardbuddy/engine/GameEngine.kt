package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
import de.sambalmueslie.boardbuddy.engine.system.*
import de.sambalmueslie.boardbuddy.workflow.api.BattleType
import de.sambalmueslie.boardbuddy.workflow.api.TechnologyStatus
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

@Singleton
class GameEngine(
    private val entityStorage: GameEntityStorage,
    private val createUnitSystem: CreateUnitSystem,
    private val createPlayerSystem: CreatePlayerSystem,
    private val combatSystem: CombatSystem,
    private val startPlayerSystem: StartPlayerSystem,
    private val battleHandSystem: BattleHandSystem,
    private val researchSystem: ResearchSystem,
    private val unitUpgradeSystem: UnitUpgradeSystem,
    private val componentModelService: GameComponentModelService,
) : GameEngineAPI {

    companion object {
        private val logger = LoggerFactory.getLogger(GameEngine::class.java)
    }

    private val damageModel = componentModelService.get(Damage::class)
    private val healthModel = componentModelService.get(Health::class)
    private val levelModel = componentModelService.get(Level::class)
    private val typeModel = componentModelService.get(Type::class)
    private val counterTypeModel = componentModelService.get(CounterType::class)
    private val nationModel = componentModelService.get(NationReference::class)
    private val governmentModel = componentModelService.get(Government::class)
    private val technologyModel = componentModelService.get(Technologies::class)
    private val unitProgressModel = componentModelService.get(UnitProgress::class)


    override fun <T : GameComponent> getComponent(entity: GameEntity, type: KClass<T>): T? {
        return componentModelService.get(type).get(entity)
    }


   override fun getUnit(entity: GameEntity): GameUnit {
        val unitEntity = entityStorage.get(entity, GameEntityType.UNIT) ?: throw WorkflowInvalidGameEntity(entity)
        val damage = damageModel.get(unitEntity)
        val health = healthModel.get(unitEntity)
        val level = levelModel.get(unitEntity)
        val type = typeModel.get(unitEntity)
        val counterType = counterTypeModel.get(unitEntity)
        return GameUnit(unitEntity, damage, health, level, type, counterType)
    }

    override fun getPlayer(entity: GameEntity): GamePlayer {
        val playerEntity = entityStorage.get(entity, GameEntityType.PLAYER) ?: throw WorkflowInvalidGameEntity(entity)
        val nation = nationModel.get(playerEntity)
        val government = governmentModel.get(playerEntity)
        val unitProgress = unitProgressModel.get(playerEntity)
        val technologies = researchSystem.getTechnologies(playerEntity)
        return GamePlayer(playerEntity, nation, government, unitProgress, technologies)
    }

    override fun exists(entity: GameEntity): Boolean {
        return entityStorage.exists(entity)
    }

    override fun delete(entity: GameEntity) {
        entityStorage.delete(entity)
    }

    override fun createUnit(player: GameSessionPlayer, unitDefinition: UnitDefinition): GameEntity {
        val entity = createUnitSystem.create(unitDefinition)
        unitUpgradeSystem.handleCreation(player, entity)
        componentModelService.persist(entity)
        return entity
    }

    override fun createPlayer(nation: Nation, unitDefinitions: List<UnitDefinition>): GameEntity {
        val entity = createPlayerSystem.create(nation, unitDefinitions)
        componentModelService.persist(entity)
        return entity
    }

    override fun combat(attackingUnit: CombatParticipant, defendingUnit: CombatParticipant): List<CombatAction> {
        return combatSystem.combat(attackingUnit, defendingUnit)
    }

    override fun research(session: GameSession, player: GameSessionPlayer, technology: Technology): List<Technology> {
        val changedTechnologies = researchSystem.research(player.entity, technology)
        unitUpgradeSystem.handleResearch(session, player, changedTechnologies)
        componentModelService.persist(player.entity)
        return changedTechnologies
    }

    fun getTechnologyStatus(player: GameSessionPlayer, technologies: List<Technology>): TechnologyStatus {
        return researchSystem.getTechnologyStatus(player.entity, technologies)
    }

    override fun determineStartPlayer(attacker: GameSessionPlayer, defender: GameSessionPlayer, type: BattleType, isWalled: Boolean): GameSessionPlayer {
        return startPlayerSystem.determine(attacker, defender, type, isWalled)
    }

    override fun determineAttackerUnits(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>): List<GameEntity> {
        return determineBattleUnits(participant, armyCount, type, units, true)
    }

    override fun determineDefenderUnits(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>): List<GameEntity> {
        return determineBattleUnits(participant, armyCount, type, units, false)
    }

    private fun determineBattleUnits(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>, isAttacker: Boolean): List<GameEntity> {
        return battleHandSystem.determine(participant, armyCount, type, units, isAttacker)
    }


}