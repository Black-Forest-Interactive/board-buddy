package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.core.technology.TechnologyService
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyEffect
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class UnitUpgradeSystem(
    private val model: GameEntityStorage,
    private val sessionService: GameSessionService,
    private val technologyService: TechnologyService,
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
    private val unitProgressModel = componentModelService.get(UnitProgress::class)


    fun handleCreation(player: GameSessionPlayer, unit: GameEntity) {
        val technologies = technologyModel.get(player.entity) ?: return
        val unitRelatedTechnologies = getUnitRelatedTechnologies(technologies.ids)
        if (unitRelatedTechnologies.isEmpty()) return

        upgradeUnit(unit, unitRelatedTechnologies)
    }

    fun handleResearch(session: GameSession, player: GameSessionPlayer, changedTechnologies: List<Technology>) {
        val unitUpgradeRequired = changedTechnologies.any { tech -> tech.effect.any { it is TechnologyEffect.UnitUnlock } }
        if (!unitUpgradeRequired) return

        val technologies = technologyModel.get(player.entity) ?: return
        val unitRelatedTechnologies = getUnitRelatedTechnologies(technologies.ids)
        if (unitRelatedTechnologies.isEmpty()) return

        val units = sessionService.getAssignedEntities(session, player)
        units.forEach { unit ->
            upgradeUnit(unit, unitRelatedTechnologies)
        }

        val current = unitProgressModel.get(player.entity)?.levels?.toMutableMap()
            ?: UnitType.entries.associateWith { 1 }.toMutableMap()
        unitRelatedTechnologies.forEach { (type, level) -> if ((current[type] ?: 1) < level) current[type] = level }
        unitProgressModel.update(player.entity, UnitProgress(current))
    }

    private fun getUnitRelatedTechnologies(technologyIds: Set<Long>): Map<UnitType, Int> {
        return technologyService.getByIds(technologyIds)
            .flatMap { tech -> tech.effect.filterIsInstance<TechnologyEffect.UnitUnlock>() }
            .groupBy { it.unitType }
            .mapValues { (_, unlocks) -> unlocks.maxOf { it.unitLevel } }
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