package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class ResearchSystem(
    private val model: GameEntityStorage,
    private val componentModelService: GameComponentModelService,
) : GameSystem {
    companion object {
        private val logger = LoggerFactory.getLogger(ResearchSystem::class.java)
    }

    private val technologyModel = componentModelService.get(Technologies::class)

    fun research(player: GameEntity, type: TechnologyType): List<TechnologyType> {
        val entity = model.get(player, GameEntityType.PLAYER) ?: throw WorkflowInvalidGameEntity(player)

        val current = technologyModel.get(entity)?.types ?: emptySet()

        if (type in current) throw EngineResearchAlreadyDiscovered(type)

        if (type.tier > 1) {
            val available = current.count { it.tier == type.tier - 1 }
            val required = current.count { it.tier == type.tier } + 2
            if (available < required) throw EngineResearchPyramidViolation(type, required, available)
        }

        val updated = Technologies(current + type)
        technologyModel.update(entity, updated)
        componentModelService.persist(entity)
        return listOf(type)
    }
}
