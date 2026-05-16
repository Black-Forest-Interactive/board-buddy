package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetNameValidationFailed
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetRepository
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetService(
    private val repository: RuleSetRepository,
    private val unitDefinitionService: RuleSetUnitDefinitionService,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<RuleSet, RuleSetChangeRequest, RuleSetData>(repository, eventService, RuleSet::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetService::class.java)
    }

    fun assignUnitDefinition(ruleSet: RuleSet, unitDefinition: UnitDefinition): RuleSet? {
        return assignUnitDefinition(ruleSet.id, unitDefinition)
    }

    fun assignUnitDefinition(ruleSetId: Long, unitDefinition: UnitDefinition): RuleSet? {
        val data = repository.findByIdOrNull(ruleSetId) ?: return null
        unitDefinitionService.assign(data, unitDefinition)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun revokeUnitDefinition(ruleSet: RuleSet, unitDefinition: UnitDefinition): RuleSet? {
        return revokeUnitDefinition(ruleSet.id, unitDefinition)
    }

    fun revokeUnitDefinition(ruleSetId: Long, unitDefinition: UnitDefinition): RuleSet? {
        val data = repository.findByIdOrNull(ruleSetId) ?: return null
        unitDefinitionService.revoke(data, unitDefinition)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    override fun convert(data: RuleSetData): RuleSet {
        return data.convert(unitDefinitionService.getAssignedUnitDefinitions(data))
    }

    override fun createData(request: RuleSetChangeRequest): RuleSetData {
        return RuleSetData(0, request.name, timeProvider.currentTime())
    }

    override fun updateData(existing: RuleSetData, request: RuleSetChangeRequest): RuleSetData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: RuleSetChangeRequest) {
        if (request.name.isBlank()) throw RuleSetNameValidationFailed(request.name)
    }

    override fun deleteDependencies(data: RuleSetData) {
        unitDefinitionService.revokeAll(data)
    }
}