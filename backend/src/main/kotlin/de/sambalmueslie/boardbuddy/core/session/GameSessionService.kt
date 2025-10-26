package de.sambalmueslie.boardbuddy.core.session

import de.sambalmueslie.boardbuddy.core.common.BaseEntityService
import de.sambalmueslie.boardbuddy.core.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionChangeRequest
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionNameValidationFailed
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionRuleSetValidationFailed
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionData
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameSessionService(
    private val repository: GameSessionRepository,
    private val sessionPlayerService: GameSessionPlayerService,

    private val playerService: PlayerService,
    private val gameService: GameService,
    private val ruleSetService: RuleSetService,

    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<GameSession, GameSessionChangeRequest, GameSessionData>(repository, eventService, GameSession::class) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameSessionService::class.java)
    }

    fun assignPlayer(gameSession: GameSession, player: Player): GameSession? {
        return assignPlayer(gameSession.id, player)
    }

    fun assignPlayer(gameSessionId: Long, player: Player): GameSession? {
        val data = repository.findByIdOrNull(gameSessionId) ?: return null
        sessionPlayerService.assign(data, player)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun revokePlayer(gameSession: GameSession, player: Player): GameSession? {
        return revokePlayer(gameSession.id, player)
    }

    fun revokePlayer(gameSessionId: Long, player: Player): GameSession? {
        val data = repository.findByIdOrNull(gameSessionId) ?: return null
        sessionPlayerService.revoke(data, player)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    override fun convert(data: GameSessionData): GameSession {
        val host = playerService.get(data.hostId) ?: throw IllegalArgumentException("Host not found")
        val participants = sessionPlayerService.getAssignedPlayers(data)
        val game = gameService.get(data.gameId) ?: throw IllegalArgumentException("Game not found")
        val ruleSet = ruleSetService.get(data.ruleSetId) ?: throw IllegalArgumentException("RuleSet not found")
        return data.convert(host, participants, game, ruleSet)
    }

    override fun createData(request: GameSessionChangeRequest): GameSessionData {
        return GameSessionData(0, "", request.name, request.host.id, request.game.id, request.ruleSet.id, timeProvider.currentTime())
    }

    override fun updateData(existing: GameSessionData, request: GameSessionChangeRequest): GameSessionData {
        return existing.update(request, timeProvider.currentTime())
    }


    override fun validate(request: GameSessionChangeRequest) {
        if (request.name.isBlank()) throw GameSessionNameValidationFailed(request.name)
        if (!request.game.ruleSets.contains(request.ruleSet)) throw GameSessionRuleSetValidationFailed(request.game, request.ruleSet)
    }

    override fun deleteDependencies(data: GameSessionData) {
        sessionPlayerService.revokeAll(data)
    }
}