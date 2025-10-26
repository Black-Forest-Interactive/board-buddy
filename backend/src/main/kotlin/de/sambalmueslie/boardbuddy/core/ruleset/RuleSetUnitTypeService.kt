package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetUnitTypeRelation
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetUnitTypeRelationRepository
import de.sambalmueslie.boardbuddy.core.unit.UnitTypeService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitType
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
        eventService.register(UnitType::class, object : EventConsumer<UnitType> {
            override fun created(obj: UnitType) {
                // intentionally left empty
            }

            override fun updated(obj: UnitType) {
                // intentionally left empty
            }

            override fun deleted(obj: UnitType) {
                repository.deleteByUnitTypeId(obj.id)
            }
        })
    }

    internal fun assign(ruleSet: RuleSetData, unitType: UnitType) {
        val existing = repository.findByRuleSetIdAndUnitTypeId(ruleSet.id, unitType.id)
        if (existing != null) return

        val relation = RuleSetUnitTypeRelation(ruleSet.id, unitType.id)
        repository.save(relation)
    }

    internal fun revoke(ruleSet: RuleSetData, unitType: UnitType) {
        repository.deleteByRuleSetIdAndUnitTypeId(ruleSet.id, unitType.id)
    }

    internal fun getAssignedUnitTypes(data: RuleSetData): List<UnitType> {
        val relations = repository.findByRuleSetId(data.id)
        val unitTypeIds = relations.map { it.unitTypeId }.toSet()
        return unitTypeService.getByIds(unitTypeIds)
    }

    internal fun revokeAll(data: RuleSetData) {
        repository.deleteByRuleSetId(data.id)
    }
}