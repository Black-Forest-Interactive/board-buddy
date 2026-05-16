package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameGateway(
    private val service: GameService,
    private val ruleSetService: RuleSetService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: GameChangeRequest) = service.create(request)
    fun update(id: Long, request: GameChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)

    fun assignRuleSet(gameId: Long, ruleSetId: Long) =
        ruleSetService.get(ruleSetId)?.let { service.assignRuleSet(gameId, it) }

    fun revokeRuleSet(gameId: Long, ruleSetId: Long) =
        ruleSetService.get(ruleSetId)?.let { service.revokeRuleSet(gameId, it) }
}
