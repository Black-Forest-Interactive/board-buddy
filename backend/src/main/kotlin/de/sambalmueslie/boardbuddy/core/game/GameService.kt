package de.sambalmueslie.boardbuddy.core.game

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.game.api.GameDescriptionValidationFailed
import de.sambalmueslie.boardbuddy.core.game.api.GameNameValidationFailed
import de.sambalmueslie.boardbuddy.core.game.db.GameData
import de.sambalmueslie.boardbuddy.core.game.db.GameRepository
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameService(
    private val repository: GameRepository,
    private val ruleSetService: GameRuleSetService,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<Game, GameChangeRequest, GameData>(repository, eventService, Game::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(GameService::class.java)
    }

    fun assignRuleSet(game: Game, ruleSet: RuleSet): Game? {
        return assignRuleSet(game.id, ruleSet)
    }

    fun assignRuleSet(gameId: Long, ruleSet: RuleSet): Game? {
        val data = repository.findByIdOrNull(gameId) ?: return null
        ruleSetService.assign(data, ruleSet)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun revokeRuleSet(game: Game, ruleSet: RuleSet): Game? {
        return revokeRuleSet(game.id, ruleSet)
    }

    fun revokeRuleSet(gameId: Long, ruleSet: RuleSet): Game? {
        val data = repository.findByIdOrNull(gameId) ?: return null
        ruleSetService.revoke(data, ruleSet)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    override fun convert(data: GameData): Game {
        return data.convert(ruleSetService.getAssignedRuleSets(data))
    }

    override fun createData(request: GameChangeRequest): GameData {
        return GameData(0, request.name, request.description, timeProvider.currentTime())
    }

    override fun updateData(existing: GameData, request: GameChangeRequest): GameData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: GameChangeRequest) {
        if (request.name.isBlank()) throw GameNameValidationFailed(request.name)
        if (request.description.isBlank()) throw GameDescriptionValidationFailed(request.description)
    }

    override fun deleteDependencies(data: GameData) {
        ruleSetService.revokeAll(data)
    }


}