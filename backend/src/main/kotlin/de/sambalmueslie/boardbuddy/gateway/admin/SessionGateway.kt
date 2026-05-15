package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionChangeRequest
import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class SessionGateway(
    private val service: GameSessionService,
    private val workflowService: WorkflowService,
    private val playerService: PlayerService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SessionGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: GameSessionChangeRequest) = service.create(request)
    fun update(id: Long, request: GameSessionChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)

}
