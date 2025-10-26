package de.sambalmueslie.boardbuddy.core.game

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.game.db.GameData
import de.sambalmueslie.boardbuddy.core.game.db.GameRuleSetRelation
import de.sambalmueslie.boardbuddy.core.game.db.GameRuleSetRelationRepository
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameRuleSetService(
    private val repository: GameRuleSetRelationRepository,
    private val ruleSetService: RuleSetService,
    eventService: EventService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameRuleSetService::class.java)
    }

    init {
        eventService.register(RuleSet::class, object : EventConsumer<RuleSet> {
            override fun created(obj: RuleSet) {
                // intentionally left empty
            }

            override fun updated(obj: RuleSet) {
                // intentionally left empty
            }

            override fun deleted(obj: RuleSet) {
                repository.deleteByRuleSetId(obj.id)
            }
        })
    }


    internal fun assign(game: GameData, ruleSet: RuleSet) {
        val existing = repository.findByGameIdAndRuleSetId(game.id, ruleSet.id)
        if (existing != null) return

        val relation = GameRuleSetRelation(game.id, ruleSet.id)
        repository.save(relation)
    }

    internal fun revoke(game: GameData, ruleSet: RuleSet) {
        repository.deleteByGameIdAndRuleSetId(game.id, ruleSet.id)
    }

    internal fun getAssignedRuleSets(data: GameData): List<RuleSet> {
        val relations = repository.findByGameId(data.id)
        val ruleSetIds = relations.map { it.ruleSetId }.toSet()
        return ruleSetService.getByIds(ruleSetIds)
    }

    internal fun revokeAll(data: GameData) {
        repository.deleteByGameId(data.id)
    }


}