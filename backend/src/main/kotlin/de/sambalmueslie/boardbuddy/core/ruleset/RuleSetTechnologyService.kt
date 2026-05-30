package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetTechnologyRelation
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetTechnologyRelationRepository
import de.sambalmueslie.boardbuddy.core.technology.TechnologyService
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetTechnologyService(
    private val repository: RuleSetTechnologyRelationRepository,
    private val technologyService: TechnologyService,
    eventService: EventService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetTechnologyService::class.java)
    }

    init {
        eventService.register(Technology::class, object : EventConsumer<Technology> {
            override fun created(obj: Technology) {
                // intentionally left empty
            }

            override fun updated(obj: Technology) {
                // intentionally left empty
            }

            override fun deleted(obj: Technology) {
                repository.deleteByTechnologyId(obj.id)
            }
        })
    }

    internal fun assign(ruleSet: RuleSetData, technology: Technology) {
        val existing = repository.findByRuleSetIdAndTechnologyId(ruleSet.id, technology.id)
        if (existing != null) return

        val relation = RuleSetTechnologyRelation(ruleSet.id, technology.id)
        repository.save(relation)
    }

    internal fun revoke(ruleSet: RuleSetData, technology: Technology) {
        repository.deleteByRuleSetIdAndTechnologyId(ruleSet.id, technology.id)
    }

    internal fun getAssignedTechnologys(data: RuleSetData): List<Technology> {
        val relations = repository.findByRuleSetId(data.id)
        val technologyIds = relations.map { it.technologyId }.toSet()
        return technologyService.getByIds(technologyIds)
    }

    internal fun revokeAll(data: RuleSetData) {
        repository.deleteByRuleSetId(data.id)
    }
}