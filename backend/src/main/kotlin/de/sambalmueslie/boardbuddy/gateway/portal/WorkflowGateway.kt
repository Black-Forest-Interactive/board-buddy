package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowGateway(
    private val service: WorkflowService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowGateway::class.java)
    }

    fun get(id: String) = service.get(id)
    fun create(request: WorkflowCreateRequest) = service.create(request)
    fun join(id: String, request: WorkflowPlayerJoinRequest) = service.join(id, request)
    fun createUnit(id: String, request: WorkflowCreateUnitRequest) = service.createUnit(id, request)
    fun battleStart(id: String, request: WorkflowBattleStartRequest) = service.battleStart(id, request)
    fun battleCreateFront(id: String, request: WorkflowBattleCreateFrontRequest) = service.battleCreateFront(id, request)
    fun battleAttackFront(id: String, request: WorkflowBattleAttackFrontRequest) = service.battleAttackFront(id, request)
    fun battleFinish(id: String) = service.battleFinish(id)
    fun getParticipantsInfo(id: String) = service.getParticipantsInfo(id)
    fun getBattleInfo(id: String) = service.getBattleInfo(id)
}