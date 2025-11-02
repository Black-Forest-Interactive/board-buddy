package de.sambalmueslie.boardbuddy.core.workflow

import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.unit.UnitTypeService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitType
import de.sambalmueslie.boardbuddy.core.workflow.api.WorkflowInvalidUnitType
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowUnitTypeService(
    private val unitTypeService: UnitTypeService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowUnitTypeService::class.java)
    }

    fun get(session: GameSession, unitTypeId: Long): UnitType {
        val unitType = unitTypeService.get(unitTypeId) ?: throw WorkflowInvalidUnitType(unitTypeId)
        val ruleSet = session.ruleSet
        if (!ruleSet.unitTypes.any { it.id == unitType.id }) throw WorkflowInvalidUnitType(unitType.id)
        return unitType
    }
}