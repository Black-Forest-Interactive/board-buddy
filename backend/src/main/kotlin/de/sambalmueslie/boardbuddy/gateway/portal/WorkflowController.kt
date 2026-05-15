package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.workflow.api.*
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/portal/workflow")
@Tag(name = "Portal Workflow API")
class WorkflowController(private val gateway: WorkflowGateway) {

    @Get("{id}")
    fun get(id: String) = gateway.get(id)

    @Post
    fun create(@Body request: WorkflowCreateRequest) = gateway.create(request)

    @Post("{id}/join")
    fun join(id: String, @Body request: WorkflowPlayerJoinRequest) = gateway.join(id, request)

    @Post("{id}/unit")
    fun createUnit(id: String, @Body request: WorkflowCreateUnitRequest) = gateway.createUnit(id, request)

    @Post("{id}/battle/start")
    fun battleStart(id: String, @Body request: WorkflowBattleStartRequest) = gateway.battleStart(id, request)

    @Post("{id}/battle/front")
    fun battleCreateFront(id: String, @Body request: WorkflowBattleCreateFrontRequest) = gateway.battleCreateFront(id, request)

    @Post("{id}/battle/attack")
    fun battleAttackFront(id: String, @Body request: WorkflowBattleAttackFrontRequest) = gateway.battleAttackFront(id, request)

    @Post("{id}/battle/finish")
    fun battleFinish(id: String) = gateway.battleFinish(id)

    @Get("{id}/participants")
    fun getParticipantsInfo(id: String) = gateway.getParticipantsInfo(id)

    @Get("{id}/battle")
    fun getBattleInfo(id: String) = gateway.getBattleInfo(id)
}