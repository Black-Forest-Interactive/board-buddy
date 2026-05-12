package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetUnitDefinitionRelation
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetUnitDefinitionRelationRepository
import de.sambalmueslie.boardbuddy.core.unit.UnitDefinitionService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetUnitDefinitionService(
    private val repository: RuleSetUnitDefinitionRelationRepository,
    private val unitDefinitionService: UnitDefinitionService,
    eventService: EventService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetUnitDefinitionService::class.java)
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
                repository.deleteByUnitDefinitionId(obj.id)
            }
        })
    }

    internal fun assign(ruleSet: RuleSetData, unitDefinition: UnitDefinition) {
        val existing = repository.findByRuleSetIdAndUnitDefinitionId(ruleSet.id, unitDefinition.id)
        if (existing != null) return

        val relation = RuleSetUnitDefinitionRelation(ruleSet.id, unitDefinition.id)
        repository.save(relation)
    }

    internal fun revoke(ruleSet: RuleSetData, unitDefinition: UnitDefinition) {
        repository.deleteByRuleSetIdAndUnitDefinitionId(ruleSet.id, unitDefinition.id)
    }

    internal fun getAssignedUnitDefinitions(data: RuleSetData): List<UnitDefinition> {
        val relations = repository.findByRuleSetId(data.id)
        val unitDefinitionIds = relations.map { it.unitDefinitionId }.toSet()
        return unitDefinitionService.getByIds(unitDefinitionIds)
    }

    internal fun revokeAll(data: RuleSetData) {
        repository.deleteByRuleSetId(data.id)
    }
}