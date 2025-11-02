package de.sambalmueslie.boardbuddy.core.workflow

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.workflow.api.*
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
        if (!session.participants.contains(player)) throw WorkflowPlayerActionForbidden(player.id)
        return player
    }

    fun join(session: GameSession, request: WorkflowPlayerJoinRequest): Player {
        if (session.participants.any { it.name == request.name }) throw WorkflowPlayerJoinError()
        val player = playerService.create(PlayerChangeRequest(request.name))
        sessionService.assignPlayer(session, player)
        return player
    }
}