package de.sambalmueslie.boardbuddy.core.workflow

import de.sambalmueslie.boardbuddy.core.engine.GameEngine
import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionChangeRequest
import de.sambalmueslie.boardbuddy.core.unit.UnitInstanceService
import de.sambalmueslie.boardbuddy.core.workflow.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowService(
    private val playerService: WorkflowPlayerService,
    private val gameService: GameService,
    private val ruleSetService: RuleSetService,
    private val unitTypeService: WorkflowUnitTypeService,
    private val unitInstanceService: UnitInstanceService,
    private val sessionService: GameSessionService,
    private val engine: GameEngine
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowService::class.java)
    }


    fun create(request: WorkflowCreateRequest): Workflow {
        val host = playerService.getHost(request.hostId)
        val game = gameService.get(request.gameId) ?: throw WorkflowInvalidGame(request.gameId)
        val ruleSet = ruleSetService.get(request.ruleSetId) ?: throw WorkflowInvalidRuleSet(request.ruleSetId)
        val session = sessionService.create(GameSessionChangeRequest(request.name, host, game, ruleSet))
        return Workflow.create(session)
    }

    fun join(id: String, request: WorkflowPlayerJoinRequest): Workflow {
        val session = getSession(id)
        playerService.join(session, request)
        return get(id)
    }

    fun createUnit(id: String, request: WorkflowCreateUnitRequest): Workflow {
        val session = getSession(id)

        val player = playerService.get(session, request.playerId)
        val unitType = unitTypeService.get(session, request.unitTypeId)
        val instance = engine.createUnit(player, session, unitType)
        sessionService.assignUnit(session, player, instance)
        return get(id)
    }


    fun get(id: String): Workflow {
        val session = sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
        return Workflow.create(session)
    }

    private fun getSession(id: String): GameSession {
        return sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
    }
}