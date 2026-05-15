package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.workflow.api.*
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/admin/workflow")
@Tag(name = "Admin Workflow API")
class WorkflowController(private val gateway: WorkflowGateway) {

    @Get("{id}")
    fun get(id: String) = gateway.get(id)

    @Post
    fun create(@Body request: WorkflowCreateRequest) = gateway.create(request)

    @Post("{id}/join")
    fun join(id: String, @Body request: WorkflowPlayerJoinRequest) = gateway.join(id, request)

    @Post("{id}/player")
    fun assignPlayer(id: String, @Body request: WorkflowAssignPlayerRequest) = gateway.assignPlayer(id, request)

    @Post("{id}/unit")
    fun createUnit(id: String, @Body request: WorkflowCreateUnitRequest) = gateway.createUnit(id, request)

    @Post("{id}/battle/start")
    fun battleStart(id: String, @Body request: WorkflowBattleStartRequest) = gateway.battleStart(id, request)

    @Post("{id}/battle/unit")
    fun battleAddUnit(id: String, @Body request: WorkflowBattleAddUnitRequest) = gateway.battleAddUnit(id, request)

    @Post("{id}/battle/front")
    fun battleCreateFront(id: String, @Body request: WorkflowBattleCreateFrontRequest) = gateway.battleCreateFront(id, request)

    @Post("{id}/battle/attack")
    fun battleAttackFront(id: String, @Body request: WorkflowBattleAttackFrontRequest) = gateway.battleAttackFront(id, request)

    @Get("{id}/participants")
    fun getParticipantsInfo(id: String) = gateway.getParticipantsInfo(id)

    @Get("{id}/battle")
    fun getBattleInfo(id: String) = gateway.getBattleInfo(id)
}