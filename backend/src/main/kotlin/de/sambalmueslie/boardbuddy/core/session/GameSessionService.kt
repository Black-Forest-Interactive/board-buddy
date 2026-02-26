package de.sambalmueslie.boardbuddy.core.session

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
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
import java.util.*

@Singleton
class GameSessionService(
    private val repository: GameSessionRepository,
    private val sessionPlayerService: GameSessionPlayerService,
    private val sessionUnitService: GameSessionUnitService,

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
        return sessionUpdated(data)
    }

    fun revokePlayer(gameSession: GameSession, player: Player): GameSession? {
        return revokePlayer(gameSession.id, player)
    }

    fun revokePlayer(gameSessionId: Long, player: Player): GameSession? {
        val data = repository.findByIdOrNull(gameSessionId) ?: return null
        sessionPlayerService.revoke(data, player)
        return sessionUpdated(data)
    }

    fun assignUnit(session: GameSession, player: Player, instance: UnitInstance): GameSession? {
        return assignUnit(session.id, player, instance)
    }

    fun assignUnit(gameSessionId: Long, player: Player, instance: UnitInstance): GameSession? {
        val data = repository.findByIdOrNull(gameSessionId) ?: return null
        sessionUnitService.assign(data, player, instance)
        return sessionUpdated(data)
    }

    fun revokeUnit(gameSession: GameSession, player: Player, instance: UnitInstance): GameSession? {
        return revokeUnit(gameSession.id, player, instance)
    }

    fun revokeUnit(gameSessionId: Long, player: Player, instance: UnitInstance): GameSession? {
        val data = repository.findByIdOrNull(gameSessionId) ?: return null
        sessionUnitService.revoke(data, player, instance)
        return sessionUpdated(data)
    }

    fun getAssignedUnits(gameSession: GameSession, player: Player): List<UnitInstance> {
        return getAssignedUnits(gameSession.id, player)
    }

    fun getAssignedUnits(gameSessionId: Long, player: Player): List<UnitInstance> {
        val data = repository.findByIdOrNull(gameSessionId) ?: return emptyList()
        return sessionUnitService.getAssignedUnits(data, player)
    }

    private fun sessionUpdated(data: GameSessionData): GameSession {
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun findByKey(key: String): GameSession? {
        return repository.findByKey(key)?.let { convert(it) }
    }

    override fun convert(data: GameSessionData): GameSession {
        val host = playerService.get(data.hostId) ?: throw IllegalArgumentException("Host not found")
        val participants = sessionPlayerService.getAssignedPlayers(data)
        val game = gameService.get(data.gameId) ?: throw IllegalArgumentException("Game not found")
        val ruleSet = ruleSetService.get(data.ruleSetId) ?: throw IllegalArgumentException("RuleSet not found")
        return data.convert(host, participants, game, ruleSet)
    }

    override fun createData(request: GameSessionChangeRequest): GameSessionData {
        return GameSessionData(0, UUID.randomUUID().toString(), request.name, request.host.id, request.game.id, request.ruleSet.id, timeProvider.currentTime())
    }

    override fun createDependencies(request: GameSessionChangeRequest, data: GameSessionData) {
        sessionPlayerService.assign(data, request.host)
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
        sessionUnitService.revokeAll(data)
    }


}