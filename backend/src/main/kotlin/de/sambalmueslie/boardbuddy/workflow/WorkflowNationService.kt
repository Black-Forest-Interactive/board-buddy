package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.nation.NationService
import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.NationReference
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowNationService(
    private val nationService: NationService,
    private val engine: GameEngine
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowNationService::class.java)
    }

    fun getAvailableNations(session: GameSession): Set<Nation> {
        val ruleSet = session.ruleSet

        val allNations = ruleSet.nations

        val takenNationIds = session.participants.mapNotNull { engine.getComponent(it.entity, NationReference::class) }.map { it.id }.toSet()

        return allNations.filterNot { takenNationIds.contains(it.id) }.toSet()
    }

    fun getNation(id: Long): Nation? {
        return nationService.get(id)
    }

    fun getAiNation(): Nation {
        return nationService.getAiNation()
    }
}