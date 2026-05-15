package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton

@Singleton
class WorkflowGateway(private val service: WorkflowService) {

    fun get(id: String) = service.get(id)
    fun create(request: WorkflowCreateRequest) = service.create(request)
    fun join(id: String, request: WorkflowPlayerJoinRequest) = service.join(id, request)
    fun assignPlayer(id: String, request: WorkflowAssignPlayerRequest) = service.assignPlayer(id, request)
    fun createUnit(id: String, request: WorkflowCreateUnitRequest) = service.createUnit(id, request)
    fun battleStart(id: String, request: WorkflowBattleStartRequest) = service.battleStart(id, request)
    fun battleAddUnit(id: String, request: WorkflowBattleAddUnitRequest) = service.battleAddUnit(id, request)
    fun battleCreateFront(id: String, request: WorkflowBattleCreateFrontRequest) = service.battleCreateFront(id, request)
    fun battleAttackFront(id: String, request: WorkflowBattleAttackFrontRequest) = service.battleAttackFront(id, request)
    fun getParticipantsInfo(id: String) = service.getParticipantsInfo(id)
    fun getBattleInfo(id: String) = service.getBattleInfo(id)
}