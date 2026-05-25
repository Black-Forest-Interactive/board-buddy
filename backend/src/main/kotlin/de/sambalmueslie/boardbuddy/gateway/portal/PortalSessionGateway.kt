package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalCreateSessionRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalJoinSessionRequest
import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.Workflow
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowAssignPlayerRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowCreateRequest
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class PortalSessionGateway(
    private val gameService: GameService,
    private val workflowService: WorkflowService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PortalSessionGateway::class.java)
    }

    fun getGames() = gameService.getAll(Pageable.UNPAGED).content

    fun createSession(playerId: Long, request: PortalCreateSessionRequest): Workflow =
        workflowService.create(WorkflowCreateRequest(request.name, playerId, request.gameId, request.ruleSetId, request.nationId))

    fun joinSession(playerId: Long, key: String, request: PortalJoinSessionRequest): Workflow {
        return workflowService.assign(key, WorkflowAssignPlayerRequest(playerId, request.nationId))
    }

    fun getSessions(playerId: Long): List<Workflow> = workflowService.getSessionsByPlayer(playerId)
}
