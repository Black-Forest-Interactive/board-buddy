package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowPlayerService(
    private val playerService: PlayerService,
    private val sessionService: GameSessionService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowPlayerService::class.java)
    }

    fun getHost(hostId: Long): Player {
        return playerService.get(hostId) ?: throw WorkflowInvalidHost(hostId)
    }

    fun get(session: GameSession, playerId: Long): Player {
        val player = playerService.get(playerId) ?: throw WorkflowInvalidPlayer(playerId)
        if (session.participants.find { it.player == player } == null) throw WorkflowPlayerActionForbidden(player.id)
        return player
    }

    fun join(session: GameSession, request: WorkflowPlayerJoinRequest, playerEntity: GameEntity): Player {
        if (session.participants.any { it.player.name == request.name }) throw WorkflowPlayerJoinError()
        val player = playerService.create(PlayerChangeRequest(request.name))
        sessionService.assignPlayer(session, player, playerEntity)
        return player
    }

    fun join(session: GameSession, player: Player, playerEntity: GameEntity): Player {
        if (session.participants.any { it.player.id == player.id }) throw WorkflowPlayerJoinError()
        sessionService.assignPlayer(session, player, playerEntity)
        return player
    }
}