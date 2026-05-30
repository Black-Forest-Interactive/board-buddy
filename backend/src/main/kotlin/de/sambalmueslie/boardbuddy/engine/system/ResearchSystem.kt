package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.technology.TechnologyService
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
import de.sambalmueslie.boardbuddy.workflow.api.TechnologyStatus
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class ResearchSystem(
    private val model: GameEntityStorage,
    private val componentModelService: GameComponentModelService,
    private val technologyService: TechnologyService,
) : GameSystem {
    companion object {
        private val logger = LoggerFactory.getLogger(ResearchSystem::class.java)
    }

    private val technologyModel = componentModelService.get(Technologies::class)

    fun research(player: GameEntity, technology: Technology): List<Technology> {
        val entity = model.get(player, GameEntityType.PLAYER) ?: throw WorkflowInvalidGameEntity(player)

        val currentIds = technologyModel.get(entity)?.ids ?: emptySet()
        if (currentIds.contains(technology.id)) throw EngineResearchAlreadyDiscovered(technology)

        val researchedByTier = technologyService.getByIds(currentIds).groupBy { it.tier }.mapValues { it.value.size }

        if (technology.tier > 1 && !isAvailable(technology, researchedByTier)) {
            val researchedPrev = researchedByTier[technology.tier - 1] ?: 0
            val researchedCurrent = researchedByTier[technology.tier] ?: 0
            val required = if (researchedCurrent == 0) 2 else researchedCurrent + 2
            throw EngineResearchPyramidViolation(technology, required, researchedPrev)
        }

        val updated = Technologies(currentIds + technology.id)
        technologyModel.update(entity, updated)
        componentModelService.persist(entity)
        return listOf(technology)
    }

    fun getTechnologies(player: GameEntity): List<Technology> {
        val currentIds = technologyModel.get(player)?.ids ?: emptySet()
        return technologyService.getByIds(currentIds)
    }

    fun getTechnologyStatus(player: GameEntity, technologies: List<Technology>): TechnologyStatus {
        val entity = model.get(player, GameEntityType.PLAYER) ?: throw WorkflowInvalidGameEntity(player)
        val researchedIds = technologyModel.get(entity)?.ids ?: emptySet()

        val (researched, notResearched) = technologies.partition { researchedIds.contains(it.id) }
        val researchedByTier = researched.groupBy { it.tier }.mapValues { it.value.size }

        val available = mutableListOf<Technology>()
        val blocked = mutableListOf<Technology>()

        notResearched.forEach { tech ->
            if (isAvailable(tech, researchedByTier)) available.add(tech) else blocked.add(tech)
        }

        return TechnologyStatus(researched, available, blocked)
    }

    private fun isAvailable(tech: Technology, researchedByTier: Map<Int, Int>): Boolean {
        if (tech.tier == 1) return true
        val researchedPrev = researchedByTier[tech.tier - 1] ?: 0
        val researchedCurrent = researchedByTier[tech.tier] ?: 0
        return (researchedCurrent == 0 && researchedPrev >= 2) || (researchedCurrent > 0 && researchedPrev - researchedCurrent > 1)
    }
}
