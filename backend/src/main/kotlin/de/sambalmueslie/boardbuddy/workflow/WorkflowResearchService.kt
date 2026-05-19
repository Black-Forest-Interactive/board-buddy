package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowInvalidPlayer
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowResearchRequest
import jakarta.inject.Singleton

@Singleton
class WorkflowResearchService(
    private val playerService: PlayerService,
    private val engine: GameEngine
) {

    fun research(session: GameSession, request: WorkflowResearchRequest) {
        val player = getAndValidatePlayer(session, request.playerId)
        engine.research(session, player, request.technology)
    }


    private fun getAndValidatePlayer(session: GameSession, playerId: Long): GameSessionPlayer {
        val player = playerService.get(playerId) ?: throw WorkflowInvalidPlayer(playerId)
        val participant = session.participants.find { it.player.id == player.id } ?: throw WorkflowInvalidPlayer(player.id)
        return participant
    }
}