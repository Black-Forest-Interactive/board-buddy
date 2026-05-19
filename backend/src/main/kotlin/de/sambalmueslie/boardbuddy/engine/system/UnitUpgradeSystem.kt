package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.api.TechnologyType.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class UnitUpgradeSystem(
    private val model: GameEntityStorage,
    private val sessionService: GameSessionService,
    componentModelService: GameComponentModelService,
) : GameSystem {

    companion object {
        private val logger = LoggerFactory.getLogger(UnitUpgradeSystem::class.java)
    }

    private val damageModel = componentModelService.get(Damage::class)
    private val healthModel = componentModelService.get(Health::class)
    private val levelModel = componentModelService.get(Level::class)
    private val typeModel = componentModelService.get(Type::class)
    private val technologyModel = componentModelService.get(Technologies::class)


    private val unitRankUnlocks: Map<TechnologyType, Pair<UnitType, Int>> = mapOf(
        DEMOCRACY to (UnitType.INFANTRY to 2),
        CHIVALRY to (UnitType.MOUNTED to 2),
        MATHEMATICS to (UnitType.ARTILLERY to 2),
        GUNPOWDER to (UnitType.INFANTRY to 3),
        RAILROAD to (UnitType.MOUNTED to 3),
        METAL_CASTING to (UnitType.ARTILLERY to 3),
        REPLACEABLE_PARTS to (UnitType.INFANTRY to 4),
        COMBUSTION to (UnitType.MOUNTED to 4),
        BALLISTICS to (UnitType.ARTILLERY to 4),
        FLIGHT to (UnitType.AIRCRAFT to 1),
    )

    fun handleCreation(player: GameSessionPlayer, unit: GameEntity) {
        val technologies = technologyModel.get(player.entity) ?: return
        val unitRelatedTechnologies = getUnitRelatedTechnologies(technologies)
        if (unitRelatedTechnologies.isEmpty()) return

        upgradeUnit(unit, unitRelatedTechnologies)
    }

    fun handleResearch(session: GameSession, player: GameSessionPlayer, changedTechnologies: List<TechnologyType>) {
        val unitUpgradeRequired = changedTechnologies.any { unitRankUnlocks.containsKey(it) }
        if (!unitUpgradeRequired) return

        val technologies = technologyModel.get(player.entity) ?: return
        val unitRelatedTechnologies = getUnitRelatedTechnologies(technologies)
        if (unitRelatedTechnologies.isEmpty()) return

        val units = sessionService.getAssignedEntities(session, player)
        units.forEach { unit ->
            upgradeUnit(unit, unitRelatedTechnologies)
        }
    }

    private fun getUnitRelatedTechnologies(technologies: Technologies): Map<UnitType, Int> {
        val unitRelatedTechnologies = technologies.types.mapNotNull { unitRankUnlocks[it] }
            .groupBy { it.first }
            .mapValues { it.value.maxBy { v -> v.second }.second }
        return unitRelatedTechnologies
    }

    private fun upgradeUnit(unit: GameEntity, unitRelatedTechnologies: Map<UnitType, Int>) {
        val type = typeModel.get(unit) ?: return
        val unitLevel = levelModel.get(unit) ?: return
        val researchLevel = unitRelatedTechnologies[type.kind] ?: return
        if (researchLevel > unitLevel.value) {
            levelModel.update(unit, Level(researchLevel))
            val damage = damageModel.get(unit)
            if(damage != null) damageModel.update(unit, Damage(damage.amount + researchLevel))

            val health = healthModel.get(unit)
            if(health != null) healthModel.update(unit, Health(health.amount + researchLevel))
        }
    }

}