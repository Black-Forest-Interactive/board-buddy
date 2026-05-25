package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetNameValidationFailed
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetRepository
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetService(
    private val repository: RuleSetRepository,
    private val unitDefinitionService: RuleSetUnitDefinitionService,
    private val technologyService: RuleSetTechnologyService,
    private val nationService: RuleSetNationService,
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


    fun assignTechnology(ruleSet: RuleSet, technology: Technology): RuleSet? {
        return assignTechnology(ruleSet.id, technology)
    }

    fun assignTechnology(ruleSetId: Long, technology: Technology): RuleSet? {
        val data = repository.findByIdOrNull(ruleSetId) ?: return null
        technologyService.assign(data, technology)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun revokeTechnology(ruleSet: RuleSet, technology: Technology): RuleSet? {
        return revokeTechnology(ruleSet.id, technology)
    }

    fun revokeTechnology(ruleSetId: Long, technology: Technology): RuleSet? {
        val data = repository.findByIdOrNull(ruleSetId) ?: return null
        technologyService.revoke(data, technology)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }


    fun assignNation(ruleSet: RuleSet, nation: Nation): RuleSet? {
        return assignNation(ruleSet.id, nation)
    }

    fun assignNation(ruleSetId: Long, nation: Nation): RuleSet? {
        val data = repository.findByIdOrNull(ruleSetId) ?: return null
        nationService.assign(data, nation)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun revokeNation(ruleSet: RuleSet, nation: Nation): RuleSet? {
        return revokeNation(ruleSet.id, nation)
    }

    fun revokeNation(ruleSetId: Long, nation: Nation): RuleSet? {
        val data = repository.findByIdOrNull(ruleSetId) ?: return null
        nationService.revoke(data, nation)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }


    override fun convert(data: RuleSetData): RuleSet {
        return data.convert(
            unitDefinitionService.getAssignedUnitDefinitions(data),
            technologyService.getAssignedTechnologys(data),
            nationService.getAssignedNations(data)
        )
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