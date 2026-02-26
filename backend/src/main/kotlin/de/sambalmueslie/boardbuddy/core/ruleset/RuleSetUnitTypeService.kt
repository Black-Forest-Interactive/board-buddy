package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetUnitTypeRelation
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetUnitTypeRelationRepository
import de.sambalmueslie.boardbuddy.core.unit.UnitTypeService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetUnitTypeService(
    private val repository: RuleSetUnitTypeRelationRepository,
    private val unitTypeService: UnitTypeService,
    eventService: EventService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetUnitTypeService::class.java)
    }

    init {
        eventService.register(UnitDefinition::class, object : EventConsumer<UnitDefinition> {
            override fun created(obj: UnitDefinition) {
                // intentionally left empty
            }

            override fun updated(obj: UnitDefinition) {
                // intentionally left empty
            }

            override fun deleted(obj: UnitDefinition) {
                repository.deleteByUnitTypeId(obj.id)
            }
        })
    }

    internal fun assign(ruleSet: RuleSetData, unitDefinition: UnitDefinition) {
        val existing = repository.findByRuleSetIdAndUnitTypeId(ruleSet.id, unitDefinition.id)
        if (existing != null) return

        val relation = RuleSetUnitTypeRelation(ruleSet.id, unitDefinition.id)
        repository.save(relation)
    }

    internal fun revoke(ruleSet: RuleSetData, unitDefinition: UnitDefinition) {
        repository.deleteByRuleSetIdAndUnitTypeId(ruleSet.id, unitDefinition.id)
    }

    internal fun getAssignedUnitTypes(data: RuleSetData): List<UnitDefinition> {
        val relations = repository.findByRuleSetId(data.id)
        val unitTypeIds = relations.map { it.unitTypeId }.toSet()
        return unitTypeService.getByIds(unitTypeIds)
    }

    internal fun revokeAll(data: RuleSetData) {
        repository.deleteByRuleSetId(data.id)
    }
}