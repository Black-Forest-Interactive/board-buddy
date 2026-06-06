package de.sambalmueslie.boardbuddy.core.session

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.session.api.*
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionData
import de.sambalmueslie.boardbuddy.core.session.db.GameSessionRepository
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@Singleton
class GameSessionService(
    private val repository: GameSessionRepository,
    private val sessionPlayerService: GameSessionPlayerService,
    private val sessionEntityService: GameSessionEntityService,

    private val playerService: PlayerService,
    private val gameService: GameService,
    private val ruleSetService: RuleSetService,

    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<GameSession, GameSessionChangeRequest, GameSessionData>(repository, eventService, GameSession::class) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameSessionService::class.java)
    }

    fun assignPlayer(gameSession: GameSession, player: Player, entity: GameEntity): GameSession? {
        return assignPlayer(gameSession.id, player, entity)
    }

    fun assignPlayer(gameSessionId: Long, player: Player, entity: GameEntity): GameSession? {
        val data = repository.findByIdOrNull(gameSessionId) ?: return null
        sessionPlayerService.assign(data, player, entity)
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


    fun assignEntity(session: GameSession, player: Player, entity: GameEntity): GameSession? {
        return assignEntity(session.id, player, entity)
    }

    fun assignEntity(gameSessionId: Long, player: Player, entity: GameEntity): GameSession? {
        val data = repository.findByIdOrNull(gameSessionId) ?: return null
        sessionEntityService.assign(data, player, entity)
        return sessionUpdated(data)
    }

    fun revokeEntity(session: GameSession, player: Player, entity: GameEntity): GameSession? {
        return revokeEntity(session.id, player, entity)
    }

    fun revokeEntity(gameSessionId: Long, player: Player, entity: GameEntity): GameSession? {
        val data = repository.findByIdOrNull(gameSessionId) ?: return null
        sessionEntityService.revoke(data, player, entity)
        return sessionUpdated(data)
    }

    fun getAssignedEntities(gameSession: GameSession, player: GameSessionPlayer): List<GameEntity> {
        return getAssignedEntities(gameSession.id, player)
    }

    fun getAssignedEntities(gameSessionId: Long, player: GameSessionPlayer): List<GameEntity> {
        val data = repository.findByIdOrNull(gameSessionId) ?: return emptyList()
        return sessionEntityService.get(data, player)
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
        sessionPlayerService.assign(data, request.host, request.hostEntity)
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
        sessionEntityService.revokeAll(data)
    }

    fun findSessionsByPlayer(playerId: Long): List<GameSession> {
        val ids = sessionPlayerService.getSessionIdsByPlayer(playerId).toSet()
        if (ids.isEmpty()) return emptyList()
        return repository.findByIdIn(ids).map { convert(it) }
    }

    fun findInactiveSince(cutoff: LocalDateTime): List<GameSession> {
        return repository.findInactiveSince(cutoff).map { convert(it) }
    }

    fun getAllEntities(sessionId: Long): List<GameEntity> {
        val data = repository.findByIdOrNull(sessionId) ?: return emptyList()
        return sessionEntityService.getAll(data) + sessionPlayerService.getAllEntityIds(data)
    }

}