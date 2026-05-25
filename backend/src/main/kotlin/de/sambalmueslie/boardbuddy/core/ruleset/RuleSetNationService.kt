package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.nation.NationService
import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetNationRelation
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetNationRelationRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetNationService(
    private val repository: RuleSetNationRelationRepository,
    private val nationService: NationService,
    eventService: EventService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetNationService::class.java)
    }


    init {
        eventService.register(Nation::class, object : EventConsumer<Nation> {
            override fun created(obj: Nation) {
                // intentionally left empty
            }

            override fun updated(obj: Nation) {
                // intentionally left empty
            }

            override fun deleted(obj: Nation) {
                repository.deleteByNationId(obj.id)
            }
        })
    }

    internal fun assign(ruleSet: RuleSetData, nation: Nation) {
        val existing = repository.findByRuleSetIdAndNationId(ruleSet.id, nation.id)
        if (existing != null) return

        val relation = RuleSetNationRelation(ruleSet.id, nation.id)
        repository.save(relation)
    }

    internal fun revoke(ruleSet: RuleSetData, nation: Nation) {
        repository.deleteByRuleSetIdAndNationId(ruleSet.id, nation.id)
    }

    internal fun getAssignedNations(data: RuleSetData): List<Nation> {
        val relations = repository.findByRuleSetId(data.id)
        val nationIds = relations.map { it.nationId }.toSet()
        return nationService.getByIds(nationIds)
    }

    internal fun revokeAll(data: RuleSetData) {
        repository.deleteByRuleSetId(data.id)
    }
}