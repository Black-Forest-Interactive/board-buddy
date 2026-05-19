package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.core.unit.UnitDefinitionService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowCreateUnitRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowInvalidPlayer
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowInvalidUnitDefinition
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowUnitService(
    private val playerService: PlayerService,
    private val unitDefinitionService: UnitDefinitionService,

    private val sessionService: GameSessionService,
    private val engine: GameEngine
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowUnitService::class.java)
    }

    fun get(session: GameSession, unitTypeId: Long): UnitDefinition {
        val unitType = unitDefinitionService.get(unitTypeId) ?: throw WorkflowInvalidUnitDefinition(unitTypeId)
        val ruleSet = session.ruleSet
        if (!ruleSet.unitDefinitions.any { it.id == unitType.id }) throw WorkflowInvalidUnitDefinition(unitType.id)
        return unitType
    }

    fun createUnit(session: GameSession, request: WorkflowCreateUnitRequest) {
        val player = getAndValidatePlayer(session, request.playerId)
        val unitType = get(session, request.unitTypeId)
        val entity = engine.createUnit(player, unitType)
        sessionService.assignEntity(session, player.player, entity)
    }


    private fun getAndValidatePlayer(session: GameSession, playerId: Long): GameSessionPlayer {
        val player = playerService.get(playerId) ?: throw WorkflowInvalidPlayer(playerId)
        val participant = session.participants.find { it.player.id == player.id } ?: throw WorkflowInvalidPlayer(player.id)
        return participant
    }
}