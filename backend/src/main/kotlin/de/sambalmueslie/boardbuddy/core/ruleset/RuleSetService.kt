package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.core.common.BaseEntityService
import de.sambalmueslie.boardbuddy.core.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetNameValidationFailed
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetRepository
import de.sambalmueslie.boardbuddy.core.unit.api.UnitType
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetService(
    private val repository: RuleSetRepository,
    private val unitTypeService: RuleSetUnitTypeService,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<RuleSet, RuleSetChangeRequest, RuleSetData>(repository, eventService, RuleSet::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetService::class.java)
    }

    fun assignUnitType(ruleSet: RuleSet, unitType: UnitType): RuleSet? {
        return assignUnitType(ruleSet.id, unitType)
    }

    fun assignUnitType(ruleSetId: Long, unitType: UnitType): RuleSet? {
        val data = repository.findByIdOrNull(ruleSetId) ?: return null
        unitTypeService.assign(data, unitType)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun revokeUnitType(ruleSet: RuleSet, unitType: UnitType): RuleSet? {
        return revokeUnitType(ruleSet.id, unitType)
    }

    fun revokeUnitType(ruleSetId: Long, unitType: UnitType): RuleSet? {
        val data = repository.findByIdOrNull(ruleSetId) ?: return null
        unitTypeService.revoke(data, unitType)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    override fun convert(data: RuleSetData): RuleSet {
        return data.convert(unitTypeService.getAssignedUnitTypes(data))
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
        unitTypeService.revokeAll(data)
    }
}