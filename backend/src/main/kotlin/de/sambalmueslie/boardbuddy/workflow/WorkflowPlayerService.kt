package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowPlayerService(
    private val playerService: PlayerService,
    private val sessionService: GameSessionService,
    private val nationService: WorkflowNationService,
    private val engine: GameEngine
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowPlayerService::class.java)
    }

    fun createHost(hostId: Long, nationId: Long, unitDefinitions: List<UnitDefinition>): GameSessionPlayer {
        val player = playerService.get(hostId) ?: throw WorkflowInvalidHost(hostId)
        val nation = nationService.getNation(nationId) ?: throw WorkflowInvalidHost(hostId)
        val entity = engine.createPlayer(nation, unitDefinitions)
        return GameSessionPlayer(player, entity)
    }

    fun get(session: GameSession, playerId: Long): Player {
        val player = playerService.get(playerId) ?: throw WorkflowInvalidPlayer(playerId)
        if (session.participants.find { it.player == player } == null) throw WorkflowPlayerActionForbidden(player.id)
        return player
    }

    fun join(session: GameSession, request: WorkflowPlayerJoinRequest): GameSessionPlayer {
        if (session.participants.any { it.player.name == request.name }) throw WorkflowPlayerJoinError()
        val player = playerService.create(PlayerChangeRequest(request.name))
        return assign(session, player, request.nationId)
    }

    fun assign(session: GameSession, request: WorkflowAssignPlayerRequest): GameSessionPlayer {
        val player = playerService.get(request.playerId) ?: throw WorkflowPlayerJoinError()
        return assign(session, player, request.nationId)
    }

    private fun assign(session: GameSession, player: Player, nationId: Long): GameSessionPlayer {
        val nation = nationService.getAvailableNations(session).find { it.id == nationId } ?: throw WorkflowPlayerJoinError()
        return assign(session, player, nation)
    }

    private fun assign(session: GameSession, player: Player, nation: Nation): GameSessionPlayer {
        if (session.participants.any { it.player.id == player.id }) throw WorkflowPlayerJoinError()
        val playerEntity = engine.createPlayer(nation, session.ruleSet.unitDefinitions)
        sessionService.assignPlayer(session, player, playerEntity)
        return GameSessionPlayer(player, playerEntity)
    }
}