package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.workflow.api.*
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleAttackFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleStartRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller(value = "/api/portal/workflow")
@Tag(name = "Portal Workflow API")
class WorkflowController(private val gateway: WorkflowGateway) {

    @Get("{id}")
    fun get(id: String) = gateway.get(id)

    @Get("{id}/og-preview")
    @Produces(MediaType.TEXT_HTML)
    fun getOgPreview(id: String): HttpResponse<String> = gateway.getOgPreview(id)

    @Get("{id}/nations")
    fun getAvailableNations(id: String) = gateway.getAvailableNations(id)

    @Get("{id}/technology-status")
    fun getTechnologyStatus(@CookieValue("player-id") playerId: Long, id: String) = gateway.getTechnologyStatus(id, playerId)

    @Post
    fun create(@Body request: WorkflowCreateRequest) = gateway.create(request)

    @Post("{id}/join")
    fun join(id: String, @Body request: WorkflowPlayerJoinRequest) = gateway.join(id, request)

    @Post("{id}/unit")
    fun createUnit(id: String, @Body request: WorkflowCreateUnitRequest) = gateway.createUnit(id, request)

    @Post("{id}/research")
    fun research(id: String, @Body request: WorkflowResearchRequest) = gateway.research(id, request)

    @Post("{id}/battle/start")
    fun battleStart(id: String, @Body request: WorkflowBattleStartRequest) = gateway.battleStart(id, request)

    @Post("{id}/battle/cancel")
    fun battleCancel(id: String) = gateway.battleCancel(id)

    @Post("{id}/battle/front")
    fun battleCreateFront(@CookieValue("player-id") playerId: Long, id: String, @Body request: WorkflowBattleCreateFrontRequest) =
        gateway.battleCreateFront(id, request, playerId)

    @Post("{id}/battle/attack")
    fun battleAttackFront(@CookieValue("player-id") playerId: Long, id: String, @Body request: WorkflowBattleAttackFrontRequest) =
        gateway.battleAttackFront(id, request, playerId)

    @Post("{id}/battle/finish")
    fun battleFinish(id: String) = gateway.battleFinish(id)

    @Get("{id}/participants")
    fun getParticipantsInfo(id: String) = gateway.getParticipantsInfo(id)

    @Get("{id}/my-info")
    fun getMyInfo(@CookieValue("player-id") playerId: Long, id: String) = gateway.getMyInfo(id, playerId)

    @Get("{id}/battle")
    fun getBattleInfo(@CookieValue("player-id") playerId: Long, id: String) = gateway.getPortalBattle(id, playerId)
}
