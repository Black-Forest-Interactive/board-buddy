package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.unit.UnitDefinitionService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowInvalidUnitDefinition
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowUnitDefinitionService(
    private val unitTypeService: UnitDefinitionService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowUnitDefinitionService::class.java)
    }

    fun get(session: GameSession, unitTypeId: Long): UnitDefinition {
        val unitType = unitTypeService.get(unitTypeId) ?: throw WorkflowInvalidUnitDefinition(unitTypeId)
        val ruleSet = session.ruleSet
        if (!ruleSet.unitDefinitions.any { it.id == unitType.id }) throw WorkflowInvalidUnitDefinition(unitType.id)
        return unitType
    }
}