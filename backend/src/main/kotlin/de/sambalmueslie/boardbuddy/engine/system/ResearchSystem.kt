package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.technology.TechnologyService
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
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
        val alreadyDiscovered = currentIds.contains(technology.id)
        if (alreadyDiscovered) throw EngineResearchAlreadyDiscovered(technology)

        val current = technologyService.getByIds(currentIds)

        if (technology.tier > 1) {
            val available = current.count { it.tier == technology.tier - 1 }
            val required = current.count { it.tier == technology.tier } + 2
            if (available < required) throw EngineResearchPyramidViolation(technology, required, available)
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
}
